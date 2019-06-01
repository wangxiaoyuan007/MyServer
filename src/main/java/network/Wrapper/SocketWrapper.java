package network.Wrapper;

import network.NIOContext;
import network.Pool;

import java.nio.channels.SocketChannel;

/**
 * @auther: wxy
 * @date: 2019/5/30 11:04
 */
public class SocketWrapper {
    private  NIOContext nioContext;
    private SocketChannel socketChannel;
    private Pool pool;


    public SocketWrapper(NIOContext nioContext, SocketChannel socketChannel, Pool pool) {
        this.nioContext = nioContext;
        this.socketChannel = socketChannel;
        this.pool = pool;
    }

    public NIOContext getNioContext() {
        return nioContext;
    }

    public void setNioContext(NIOContext nioContext) {
        this.nioContext = nioContext;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }


    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}
