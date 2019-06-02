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
 * serverSocketChannel 注册并监听accept事件
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
        //将ServerSocketChannel注册到selector上，监听客户端连接事件
        SelectionKey key = serverSocket.register(selector,SelectionKey.OP_ACCEPT);
        //绑定端口
        serverSocket.bind(new InetSocketAddress(port));
        this.nioContext = new NIOContext();
        //将Acceptor与key相绑定，以便触发事件后操作对应的serverSocket
        key.attach(new Acceptor(serverSocket,nioContext));
    }

    @Override
    public void run() {
        //以死循环方式不断监听socketchannel（客户端连接）事件
        while (!Thread.interrupted()){
            try {
                //此处阻塞，一直等待连接事件到来才被唤醒
                selector.select();
                //获得触发的事件key
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    //对于每一个触发连接的事件，分发给Acceptor进行处理
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
