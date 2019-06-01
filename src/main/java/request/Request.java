package request;

import cookie.Cookie;
import lombok.extern.slf4j.Slf4j;
import servlet.Dispatcher;
import servlet.WebApp;
import session.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @auther: wxy
 * @date: 2019/5/27 18:25
 */
@Slf4j
public class Request {

    //请求方式
    private String method;
    //请求资源
    private String url;
    //请求参数
    private Map<String, List<String>> paramterMapValues;
    private  static  final  String BLANK=" ";
    private  static  final  String CRLF="\r\n";
    private HttpSession session;
    private Cookie [] cookies;
    private Map<String,List<String>> headers;
    private Dispatcher requestHandler;

    //请求
    private String requestInfo;

    public Request() {
        method="";
        url="";
        paramterMapValues=new HashMap<>();

    }

    public Request(byte [] data) throws IOException {
        this();
        requestInfo=new String(data,0,data.length);
        //分析头部信息
        parseRequestInfo();


    }
//GET / HTTP/1.1
//Host: localhost:8088
//Connection: keep-alive
//Cache-Control: max-age=0
//Upgrade-Insecure-Requests: 1
//User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36
//Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3
//Accept-Encoding: gzip, deflate, br
//Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
//Cookie: Pycharm-b9a968c7=03cea11b-0f97-4286-8eba-267c5d2b8ddb; Webstorm-a1a76140=8a03ce36-4fa0-49bc-9cd9-bfe5391c5146
    private void parseRequestInfo() {
        if(requestInfo==null || ((requestInfo = requestInfo.trim()).equals(""))){
            return;
        }
        /**
         *从上方请求头中分析出:请求方式，请求url，请求参数
         * get 参数在头
         * post 参数在尾
         */
        //请求参数
        String paramsterString="";

        //1.获取请求方式

        String firstLine = requestInfo.substring(0,requestInfo.indexOf(CRLF));
        this.method=firstLine.substring(0,requestInfo.indexOf("/")).trim();
        String urlStr=firstLine.substring(requestInfo.indexOf("/"),requestInfo.indexOf("HTTP/"));
        if(this.method.equals("POST") || this.method.equals("post")){
            this.url=urlStr;
            paramsterString=requestInfo.substring(requestInfo.lastIndexOf(CRLF)).trim();
        }else  if(this.method.equals("GET")){
            //如果为GET方式，参数可能跟在url后面 如 /xxx?a=1&b=2
            if(urlStr.contains("?")){
                String [] urlArr= urlStr.split("\\?");
                this.url=urlArr[0];
                paramsterString=urlArr[1];
            }else {
                this.url=urlStr;
            }

        }

        //2.将请求参数封装到map内
        if(paramsterString.equals("")){
            return;
        }
        parseParams(paramsterString);
        //解析请求头
        parseHeaders(requestInfo.split(CRLF));
    }

    private void parseHeaders(String[] lines) {

        //解析请求头
        String header;
        this.headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            header = lines[i];
            if (header.equals("")) {
                break;
            }
            int colonIndex = header.indexOf(':');
            String key = header.substring(0, colonIndex);
            String[] values = header.substring(colonIndex + 2).split(",");
            headers.put(key, Arrays.asList(values));
        }
        log.debug("headers:{}", this.headers);

        //解析Cookie

        if (headers.containsKey("Cookie")) {
            String[] rawCookies = headers.get("Cookie").get(0).split("; ");
            this.cookies = new Cookie[rawCookies.length];
            for (int i = 0; i < rawCookies.length; i++) {
                String[] kv = rawCookies[i].split("=");
                this.cookies[i] = new Cookie(kv[0], kv[1]);
            }
            headers.remove("Cookie");
        } else {
            this.cookies = new Cookie[0];
        }
        log.info("Cookies:{}", Arrays.toString(cookies));
    }

    //解析 a=2?b=9
    private void parseParams(String paramsterString) {
        StringTokenizer tokenizer = new StringTokenizer(paramsterString,"&");
        while (tokenizer.hasMoreTokens()){
            String keyValue = tokenizer.nextToken();
            String [] keyValues=keyValue.split("=");
            if(keyValues.length==1){
                Arrays.copyOf(keyValues,2);
                keyValues[1]=null;
            }
            String key=keyValues[0];
            String value = keyValues[1]==null?null:decode(keyValues[1].trim(),"utf-8");
            if(!paramterMapValues.containsKey(keyValues[0])){
                ArrayList<String> values= new ArrayList<>();
                values.add(value);
                paramterMapValues.put(key.trim(),values);
            }
        }
    }

    //根据key返回参数值
    public String [] getParameterValues(String name){
        List<String> values=paramterMapValues.get(name);
        if(values==null){
            return null;
        }else{
            return  values.toArray(new String[0]);
        }

    }

    //根据key返回单个
    public String getParameter(String name){
        List<String> values=paramterMapValues.get(name);
        if(values==null){
            return null;
        }else{
            return  values.toArray(new String[0])[0];
        }

    }

    //解码
    private  String decode(String value,String code){
        try {
            return  URLDecoder.decode(value,code);
        } catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    /**
     * 如果请求报文包含以JESSIONID作为key的cookie，那么直接从上下文返回对应的session
     * 如果不存在，那么创建一个session，并在返回报文添加Set-Cookie: xxxxxxx（随机值）
     * 服务器只会在第一次响应的时候添加Set-Cookie：值，接下去就不会
     * 没有调用该方法就不会添加
     * @param createIfNotExists
     * @return HttpSession
     */
    public HttpSession getSession(boolean createIfNotExists) {
        if (session != null) {
            return session;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getKey().equals("JSESSIONID")) {
                HttpSession currentSession = WebApp.getContext().getSession(cookie.getValue());
                if (currentSession != null) {
                    session = currentSession;
                    return session;
                }
            }
        }
        if (!createIfNotExists) {
            return null;
        }
        session = WebApp.getContext().createSession(requestHandler.getResponse());
        return session;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public void setRequestHandler(Dispatcher requestHandler) {
        this.requestHandler = requestHandler;
    }
}
