package listenner;

import listenner.even.ServletContextEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther: wxy
 * @date: 2019/5/31 21:31
 */
@Slf4j
public class TestListenner implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent paramServletContextEvent) {
        log.info("初始化容器......");
    }

    @Override
    public void contextDestroyed(ServletContextEvent paramServletContextEvent) {
        log.info("容器初始化完成......");
    }
}
