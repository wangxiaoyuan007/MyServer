package servlet;

import request.Request;
import response.Response;

import java.io.IOException;

/**
 * @auther: wxy
 * @date: 2019/5/27 21:01
 */
public abstract class HttpServlet {
    public abstract void  init();
     void doService(Request request, Response response) throws IOException{
        doGet(request,response);
        doPost(request,response);
    }
    public  abstract void doGet(Request req, Response rep);

    public abstract  void doPost(Request req, Response rep);
    public abstract void destory(Request request, Response response);
}
