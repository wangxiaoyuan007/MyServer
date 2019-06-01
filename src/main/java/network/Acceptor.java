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
 * NIO serversocket开始监听
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
            SocketChannel channel = serverSocket.accept();
            if(channel != null){

                nioContext.registerToPools(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
