package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther: wxy
 * @date: 2019/5/29 22:14
 */
public class ThreadUtil {
    private    static  ExecutorService executor= Executors.newFixedThreadPool(17);;

    public static ExecutorService getExecutor() {
        return executor;
    }
}
