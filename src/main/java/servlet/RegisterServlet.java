package servlet;

import request.Request;
import response.Response;

/**
 * @auther: wxy
 * @date: 2019/5/27 22:46
 */
public class RegisterServlet extends Servlet {
    @Override
    public void doGet(Request req, Response rep) {

    }

    @Override
    public void doPost(Request req, Response rep) {
        rep.println("<html><head><title>注册响应</title></head><body>");
        rep.println("你的用户名为："+req.getParameter("uname"));
        rep.println("</body></html>");
    }
}
