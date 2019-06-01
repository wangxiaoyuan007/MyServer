package listenner.even;

import Context.ServletContext;

import java.util.EventObject;

/**
 * @auther: wxy
 * @date: 2019/5/31 21:29
 */
public class ServletContextEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ServletContextEvent(Object source) {
        super(source);
    }
    public ServletContext getServletContext () {
        return (ServletContext) super.getSource();
    }
}
