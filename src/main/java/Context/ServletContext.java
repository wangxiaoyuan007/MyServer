package Context;

import Handler.ServerletHandler;
import cookie.Cookie;
import listenner.ServletContextListener;
import listenner.even.ServletContextEvent;
import mapping.ServerletMapping;
import mapping.UrlMapping;
import response.Response;
import servlet.HttpServlet;
import servlet.WebApp;
import session.HttpSession;
import utils.UUIDUtil;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: wxy
 * @date: 2019/5/27 22:35
 */
public class ServletContext {
    //为每一个serverlet取一个别名 如Login->LoginHttpServlet
    private Map<String, HttpServlet> serverlet;
    //url->Login
    private Map<String,String> mapping;
    private ServerletHandler serverletHandler ;
    private List<ServletContextListener> listeners;
    private Map<String, HttpSession> sessions;
    private Map<String, Cookie> cookies;
    public static final int DEFAULT_SESSION_EXPIRE_TIME = 300;
    public ServletContext() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.serverlet=new HashMap<>();
        this.mapping=new HashMap<>();
        this.sessions=new ConcurrentHashMap<>();
        this.cookies=new ConcurrentHashMap<>();
        this.serverletHandler = new ServerletHandler();
        this.listeners = new ArrayList<>();
        initServerlet();
        initServletListens();
        //执行容器初始化前监听事件
        preServletContext();
        /**
         * 逻辑
         */
        //执行容器初始化后监听事件
        afterServletContext();

    }


    //初始化监听器
    private void initServletListens() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<String> listenner = serverletHandler.getListeners();
        for (String listennerClassName : listenner){
            this.listeners.add((ServletContextListener) Class.forName(listennerClassName).getConstructor().newInstance());
        }

    }


    //读取配置文件内serverlet并初始化
    private void initServerlet() {
        for(Map.Entry<String, ServerletMapping> e:serverletHandler.getServerlets().entrySet()){
            if(serverlet.get(e.getKey())==null){
                try {
                    serverlet.put(e.getKey().trim(),(HttpServlet) WebApp.class.forName(e.getValue().getServerletName()).getConstructor().newInstance());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }
        for(Map.Entry<String, UrlMapping> e:serverletHandler.getPatterns().entrySet()){
            mapping.put(e.getKey().trim(),e.getValue().getServerletName());
        }
    }

    //根据serverlet的name匹配serverlet
    public HttpServlet getServerlet(String url) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(url==null||(url.trim().equals(""))){
            return  null;
        }

        return this.serverlet.get(this.mapping.get(url));
    }

    //执行容器初始化前监听事件
    private void preServletContext() {
        for (ServletContextListener listener: listeners){
            listener.contextInitialized(new ServletContextEvent(this));
        }
    }


    //执行容器初始化后监听事件
    private void afterServletContext() {
        for (ServletContextListener listener: listeners){
            listener.contextDestroyed(new ServletContextEvent(this));
        }
    }

    public void invalidateSession(HttpSession session) {
        this.sessions.remove(session.getJsessionId());
        //这里可以再执行Session销毁后置处理器

    }

    //获取session
    public  HttpSession getSession(String JESSIONID){
        return sessions.get(JESSIONID);
    }
    //清除空闲session
    public void cleanIdleSessions() {
        for (Iterator<Map.Entry<String, HttpSession>> it = sessions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, HttpSession> entry = it.next();
            if (Duration.between(entry.getValue().getLastAccessed(), Instant.now()).getSeconds() >= DEFAULT_SESSION_EXPIRE_TIME) {
//                log.info("该session {} 已过期", entry.getKey());
                //这里可以执行session销毁后置处理器
                it.remove();
            }
        }
    }

    //创建session
    public HttpSession createSession(Response response) {
        HttpSession session = new HttpSession(UUIDUtil.uuid());
        sessions.put(session.getJsessionId(), session);
        response.addCookie(new Cookie("JSESSIONID", session.getJsessionId()));

        return session;
    }


    public Map<String, HttpServlet> getServerlet() {
        return serverlet;
    }

    public void setServerlet(Map<String, HttpServlet> serverlet) {
        this.serverlet = serverlet;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }


}
