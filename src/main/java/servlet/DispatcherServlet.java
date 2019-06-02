package servlet;

import lombok.Getter;
import request.Request;
import response.Response;
import utils.PaseHtmlUtil;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @auther: wxy
 * @date: 2019/5/27 21:17
 */
@Getter
public class DispatcherServlet implements Runnable {
    private Request request;
    private Response response;
    private SocketChannel socketChannel;
    private  int code =200;



    public DispatcherServlet(Request request, Response response, SocketChannel socketChannel) {
        this.socketChannel=socketChannel;
        this.request = request;
        this.response= response;

    }



    @Override
    public void run() {
        //重定向处理，直接跳转至html
       if(request.getUrl().trim().endsWith(".html")){
           try {
               //去掉url第一个/
               String url = request.getUrl().trim().substring(1,request.getUrl().trim().length());
               //输出整个html页面信息
               response.println(new String(PaseHtmlUtil.getHtml(url)));
               response.pushToClient(302);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }else {
           //查询与url相对应的servlet,调用doService()处理业务
           HttpServlet servlet = null;
           try {
               servlet = WebApp.getContext().getServerlet(request.getUrl());
           } catch (ClassNotFoundException e) {
               e.printStackTrace();
           } catch (IllegalAccessException e) {
               e.printStackTrace();
           } catch (InstantiationException e) {
               e.printStackTrace();
           }
           if(servlet ==null){
               //找不到对应处理
               this.code=404;
               return;
           }
           try {
               servlet.doService(request,response);
               if(socketChannel.isOpen()){
                   //response推到客户端浏览器显示处理结果
                   response.pushToClient(code);
               }

           } catch (IOException e) {
               this.code=500;
               e.printStackTrace();
           }finally {
               try {
                   socketChannel.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
    }


}
