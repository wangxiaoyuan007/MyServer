package network;

import utils.ThreadUtil;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auther: wxy
 * @date: 2019/5/29 22:30
 * 该类初始化pool线程组
 */
public class NIOContext {
    //标记应用是否运行
    private volatile  boolean isRunning = true;
    //初始值
    private  int poolSize = 5;
    List<Pool> pools ;
    private AtomicInteger pollerRotater  = new AtomicInteger(0);
    public NIOContext() throws IOException {
        this.pools = new ArrayList<>();
        initPools();
    }

    private void initPools() throws IOException {
        for(int i = 0;i < poolSize; i++ ){
            Pool pool = new Pool(this);
            pools.add(pool);
            ThreadUtil.getExecutor().execute(pool);
        }
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public   Pool getPool(){
        return  pools.get(pollerRotater.incrementAndGet()%poolSize);
    }

    public  void registerToPools(SocketChannel socket){
        getPool().register(socket);
    }

}
