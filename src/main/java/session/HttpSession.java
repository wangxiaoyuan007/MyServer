package session;

import servlet.WebApp;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: wxy
 * @date: 2019/6/1 16:34
 */
public class HttpSession {
    private Map<String,Object> attributes;
    private String JsessionId;
    private  boolean isValid;
    private Instant lastAccessed;

    public HttpSession(String jsessionId) {
        this.JsessionId = jsessionId;
        this.attributes = new HashMap<>();
        this.isValid = true;
        this.lastAccessed = Instant.now();
    }

    public  Object getAttribbute(String key){
        if(isValid){
            this.lastAccessed = Instant.now();
            return attributes.get(key);
        }
        throw new IllegalStateException("session 已经失效");
    }

    public  void setAttribute(String key,Object value){
        if(isValid){
            this.lastAccessed = Instant.now();
            attributes.put(key,value);
        }
        throw new IllegalStateException("session 已经失效");
    }
    public void invalidate() {
        this.isValid = false;
        this.attributes.clear();
        WebApp.getContext().invalidateSession(this);
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getJsessionId() {
        return JsessionId;
    }

    public boolean isValid() {
        return isValid;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
