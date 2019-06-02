package response;

import cookie.Cookie;
import enumer.HttpStatusEnum;
import servlet.DispatcherServlet;
import utils.PaseHtmlUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 封装响应头
 * @author wxy
 *
 */
public class Response {
    private  static  final  String BLANK=" ";
    private  static  final  String CRLF="\r\n";
    private List<Cookie> cookies;
    private List<Header> headers;
    private  StringBuilder headInfo;
    private  StringBuilder context;
    private  int  len;
    private  int code;
    private BufferedWriter bw;
    private DispatcherServlet requestHandler;

    public Response() {
        this.cookies = new ArrayList<>();
        this.headers = new ArrayList<>();
        headInfo=new StringBuilder();
        context=new StringBuilder();
        len=0;
    }
    public Response(OutputStream o) {
       this();
       bw= new BufferedWriter(new OutputStreamWriter(o));
    }

    /**
     * 构建响应头
     */
    public  void createHeadInfo(int code ){
        headInfo.delete(0,headInfo.length());
        //1.http协议版本，状态码，描述
        headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
        switch (code){
            case 200:
                headInfo.append("OK");
                break;
            case 404:
                headInfo.append("NOT FOUND");
                break;
            case 500:
                headInfo.append("Servert Error");

        }
        headInfo.append(CRLF);
        //2.响应头
        headInfo.append("Server:bjsxt Server/0.0.1").append(CRLF);
        headInfo.append("Date:").append(new Date()).append(CRLF);
        headInfo.append("Content-Type:text/html; charset=UTF-8").append(CRLF);
        if (headers.size() > 0) {
            for (Header header : headers) {
                headInfo.append(header.getKey()).append(":").append(BLANK).append(header.getValue()).append(CRLF);
            }
        }

        if (cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                headInfo.append("Set-Cookie:").append(BLANK).append(cookie.getKey()).append("=").append(cookie.getValue()).append(CRLF);
            }
        }
        headInfo.append("Content-Length").append(len).append(CRLF);
        headInfo.append(CRLF);
    }
    /**
     *构建正文+回车
     */
    public  Response println(String info){
        context.append(info).append(CRLF);
        len+=info.getBytes().length;
        return  this;
    }

    /**
     * 推到客户端
     */
    public void pushToClient(int code) throws IOException {
      createHeadInfo(code);
      ByteBuffer output = ByteBuffer.allocate(headInfo.toString().getBytes().length+context.toString().getBytes().length);
      output.clear();

      output.put(headInfo.toString().getBytes());
      output.put(context.toString().getBytes());
      output.flip();
      this.requestHandler.getSocketChannel().write(output);
      this.requestHandler.getSocketChannel().close();
    }

    /**
     * 重定向
     * @param url
     */
    public void sendRedirect(String url) throws IOException {

        addHeader(new Header("Location", url));
        buildContext(url);
        setStatus(HttpStatusEnum.MOVED_TEMPORARILY);
        // 刷新至客户端
        pushToClient(HttpStatusEnum.MOVED_TEMPORARILY.getCode());

    }

    private void buildContext(String url) {
        context.delete(0,context.length());
        try {
            context.append(new String(PaseHtmlUtil.getHtml(url)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setStatus(HttpStatusEnum movedTemporarily) {
        this.code = movedTemporarily.getCode();
    }

    private void addHeader(Header header) {
        this.headers.add(header);
    }


    /**
     *
     */
    public void addCookie(Cookie cookie){
        cookies.add(cookie);
    }


    public void setRequestHandler(DispatcherServlet requestHandler) {
        this.requestHandler = requestHandler;
    }
}
