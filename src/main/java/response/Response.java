package response;

import cookie.Cookie;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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

    private  StringBuilder headInfo;
    private  StringBuilder context;
    private  int  len;
    private BufferedWriter bw;
    public Response() {
        this.cookies = new ArrayList<>();
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
        headInfo.append("Content-Type:text/html;charset=UTF-8").append(CRLF);
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
    public void pushToClient(int code, SocketChannel socketChannel) throws IOException {
      createHeadInfo(code);
      ByteBuffer output = ByteBuffer.allocate(1024);
      output.clear();
      output.put(headInfo.toString().getBytes());
      output.put(context.toString().getBytes());
      output.flip();
      socketChannel.write(output);
    }

    /**
     *
     */
    public void addCookie(Cookie cookie){
        cookies.add(cookie);
    }

}
