package network;

import utils.ThreadUtil;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @auther: wxy
 * @date: 2019/5/28 21:48
 * NIO serversocket开始监听,获得与客户端交互的socketchannel并注册到selector
 */
public class Acceptor implements  Runnable{
    private  NIOContext nioContext;
    private  final  ServerSocketChannel serverSocket;
    public Acceptor(ServerSocketChannel serverSocket,NIOContext nioContext) {
        this.serverSocket = serverSocket;
        this.nioContext = nioContext;

    }

    @Override
    public void run() {
        try {
            //阻塞，当客户端连接事件发生后，获得与客户端交互的socketchanel
            SocketChannel channel = serverSocket.accept();
            if(channel != null){
                //将每个与客户端交互的socketchannel注册到已创建的Pool中
                //也就是将每个socketchannel注册到固定的几个selector上
                nioContext.registerToPools(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
