package servlet;

import Context.ServletContext;

import java.lang.reflect.InvocationTargetException;

/**
 * @auther: wxy
 * @date: 2019/5/27 22:39
 * 持有servlet上下文信息
 */
public class WebApp {
    private  static ServletContext context;
    static {
        try {
            context=new ServletContext();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static ServletContext getContext() {
        return context;
    }
}
