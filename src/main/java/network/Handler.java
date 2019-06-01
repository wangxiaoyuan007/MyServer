package network;

import request.Request;
import response.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @auther: wxy
 * @date: 2019/5/29 10:25
 */
public class Handler implements Runnable {
    private final  SocketChannel socketChannel;
    private   Selector selector;


    private   SelectionKey key;
    private volatile ByteBuffer input = ByteBuffer.allocate(1024);
    private volatile ByteBuffer output = ByteBuffer.allocate(1024);
    public Handler(SocketChannel channel) throws IOException {
        this.socketChannel = channel;
        this.socketChannel.configureBlocking(false);
        this.selector=Selector.open();
        //注册客户端读事件
        this.key = socketChannel.register(selector,SelectionKey.OP_READ);

    }

    @Override
    public void run() {
        while (selector.isOpen()&&socketChannel.isOpen()){
            try {
                Set<SelectionKey> keys = select();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isReadable()){
                        read(key);
                    }else if(key.isWritable()) {
                        write(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        socketChannel.read(input);
        if (input.position() == 0) {
            return;
        }

        input.flip();
        process();  // 对读取的数据进行业务处理
        input.clear();
        //key.interestOps(SelectionKey.OP_WRITE);  // 读取完成后监听写入事件

    }

    private void write(SelectionKey key) throws IOException {
       /* output.flip();
        if (socketChannel.isOpen()) {
            socketChannel.write(output);  // 当有写入事件时，将业务处理的结果写入到客户端Channel中
            key.channel();
            socketChannel.close();
            output.clear();
        }*/
    }

    private void process() throws IOException {
        byte[] bytes = new byte[input.remaining()];
        input.get(bytes);


        Request request = new Request(bytes);
        Response response = new Response();

        response.println("hello");

        response.pushToClient(200,socketChannel);
        String message = new String(bytes);
        System.out.println(message);
        //socketChannel.close();


    }

    private Set<SelectionKey> select() throws IOException {
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        if(keys.isEmpty()){
            int interestOps = key.interestOps();
            this.selector = Selector.open();
            this.key = socketChannel.register(selector, interestOps);
            return select();
        }

        return  keys;
    }


}
