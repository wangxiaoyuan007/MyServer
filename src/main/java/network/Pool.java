package network;

import network.Wrapper.SocketWrapper;
import request.Request;
import response.Response;
import servlet.DispatcherServlet;
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
 * pool类，包含有一个selector，用于被socketchannel注册读写事件
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
                //检查是否有新的socketchannel需要被注册到当前selector
                events();
                int select = selector.select();
                /**
                 * 此处判断selector上是否有读写事件发生还是是被刻意唤醒的
                 * <=0:没有读写事件，则该次阻塞被唤醒是因为调用wakeup(),则继续从头检查
                 * >0:表示当前socketchannel有读写事件，则处理它
                 */
                if(  select <= 0){

                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    //读事件
                    if(key.isReadable()){
                        handleRread(key);
                    }else if(key.isWritable()){
                        //写事件
                        handleWrite(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //处理读事件
    private void handleRread(SelectionKey key) throws IOException {
        SocketWrapper wrapper = (SocketWrapper) key.attachment();
        SocketChannel socketChannel = wrapper.getSocketChannel();
        socketChannel.read(input);
        if (input.position() == 0) {
            return;
        }

        input.flip();
        // 此处对读取的数据进行业务处理
        process(socketChannel);
        input.clear();
    }

    //处理读事件
    private void process(SocketChannel socketChannel) throws IOException {
        byte[] bytes = new byte[input.remaining()];
        input.get(bytes);
        /**
         * 根据读取的浏览器客户端发送的信息，封装request，并创建response
         * 再分发给相应的servlet进行业务处理
         */
        Request request = new Request(bytes);
        Response response = new Response();
        DispatcherServlet dispatcher = new DispatcherServlet(request,response,socketChannel);
        request.setRequestHandler(dispatcher);
        response.setRequestHandler(dispatcher);
        //交由线程池处理，启动dispatcherServlet进行分发处理
        ThreadUtil.getExecutor().execute(dispatcher);
        //socketChannel.close();


    }

    //与浏览器客户端交互，一般也用不到写事件
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
