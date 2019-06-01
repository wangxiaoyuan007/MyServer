package servlet;

import lombok.Getter;
import request.Request;
import response.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @auther: wxy
 * @date: 2019/5/27 21:17
 */
@Getter
public class Dispatcher implements Runnable {
    private Request request;
    private Response response;
    private SocketChannel socketChannel;
    private  int code =200;



    public Dispatcher(Request request, Response response, SocketChannel socketChannel) {
        this.socketChannel=socketChannel;
        this.request = request;
        this.response= response;

    }



    @Override
    public void run() {
        Servlet servlet = null;
            System.out.println("-----------run-----------");
        try {
            servlet = WebApp.getContext().getServerlet(request.getUrl().trim());
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
            response.pushToClient(code,socketChannel) ;
        } catch (IOException e) {
            this.code=500;
            e.printStackTrace();
        }finally {

        }
    }


}
