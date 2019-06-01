package network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @auther: wxy
 * @date: 2019/5/29 9:56
 */
@Slf4j
public class Reactor implements  Runnable {
    private  final Selector selector;
    private  NIOContext nioContext;
    private  final ServerSocketChannel serverSocket;
    private  int port;

    public Reactor(int port) throws IOException {

        serverSocket=ServerSocketChannel.open();
        //设为非阻塞方式
        serverSocket.configureBlocking(false);
        this.port = port;
        this.selector=Selector.open();
        SelectionKey key = serverSocket.register(selector,SelectionKey.OP_ACCEPT);
        serverSocket.bind(new InetSocketAddress(port));
        this.nioContext = new NIOContext();
        key.attach(new Acceptor(serverSocket,nioContext));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            try {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    dispatch(iterator.next());
                    iterator.remove();
                }
                selector.selectNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //触发的selector事件，从此入口分发到acctor执行
    private void dispatch(SelectionKey key) {
        Runnable acctor = (Runnable) key.attachment();
        acctor.run();
    }
}
