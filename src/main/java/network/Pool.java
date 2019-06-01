package network;

import network.Wrapper.SocketWrapper;
import request.Request;
import response.Response;
import servlet.Dispatcher;
import utils.ThreadUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @auther: wxy
 * @date: 2019/5/29 22:30
 */
public class Pool implements Runnable {
    private  NIOContext nioContext;
    private   Selector selector;
    private volatile ByteBuffer input = ByteBuffer.allocate(5*1024);
    private volatile ByteBuffer output = ByteBuffer.allocate(5*1024);
    private Queue<PollerEvent> events;

    public Pool( NIOContext nioContext) throws IOException {
        this.nioContext = nioContext;
        this.selector = Selector.open();
        this.events = new ConcurrentLinkedQueue<>();

    }

    @Override
    public void run() {
        while (nioContext.getIsRunning()){

            try {
                events();
                int select = selector.select();
                if(  select <= 0){

                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        handleRread(key);
                    }else if(key.isWritable()){
                        handleWrite(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRread(SelectionKey key) throws IOException {
        SocketWrapper wrapper = (SocketWrapper) key.attachment();
        SocketChannel socketChannel = wrapper.getSocketChannel();
        socketChannel.read(input);
        if (input.position() == 0) {
            return;
        }

        input.flip();
        process(socketChannel);  // 对读取的数据进行业务处理
        input.clear();
    }

    private void process(SocketChannel socketChannel) throws IOException {
        byte[] bytes = new byte[input.remaining()];
        input.get(bytes);
        Request request = new Request(bytes);
        Response response = new Response();
        Dispatcher dispatcher = new Dispatcher(request,response,socketChannel);
        request.setRequestHandler(dispatcher);
        ThreadUtil.getExecutor().execute(dispatcher);
        //socketChannel.close();


    }

    private void handleWrite(SelectionKey key) {

    }

    public NIOContext getNioContext() {
        return nioContext;
    }


    public synchronized Selector getSelector() {
        return selector;
    }

    public void register(SocketChannel socket) {
        try {
            socket.configureBlocking(false);
            SocketWrapper wrapper = new SocketWrapper(nioContext, socket, this);
            events.offer(new PollerEvent(wrapper));
            selector.wakeup();

        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private boolean events() {

        boolean result = false;
        PollerEvent pollerEvent;
        for (int i = 0, size = events.size(); i < size && (pollerEvent = events.poll()) != null; i++) {
            result = true;
            pollerEvent.run();
        }
        return result;
    }


    private static class PollerEvent implements Runnable {
        private SocketWrapper wrapper;

        public PollerEvent(SocketWrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public void run() {

            try {
                if (wrapper.getSocketChannel().isOpen()) {
                    SelectionKey key = wrapper.getSocketChannel().register(wrapper.getPool().getSelector(), SelectionKey.OP_READ);
                    key.attach(wrapper);
                } else {
                    System.out.println("socket已经被关闭，无法注册到Poller");
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }
}
