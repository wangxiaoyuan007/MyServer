package listenner;

import listenner.even.ServletContextEvent;

import java.util.EventListener;

/**
 * @auther: wxy
 * @date: 2019/5/31 21:25
 */
public interface ServletContextListener extends EventListener {
    public abstract void contextInitialized(ServletContextEvent paramServletContextEvent);
    public abstract void contextDestroyed(ServletContextEvent paramServletContextEvent);
}
