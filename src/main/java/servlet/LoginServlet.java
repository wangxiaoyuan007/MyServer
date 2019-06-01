package servlet;

import request.Request;
import response.Response;
import session.HttpSession;

/**
 * @auther: wxy
 * @date: 2019/5/27 22:44
 */
public class LoginServlet extends Servlet {
    @Override
    public void doGet(Request req, Response rep) {

    }

    @Override
    public void doPost(Request req, Response rep) {
       String name = req.getParameter("uname");
       String pwd=req.getParameter("pwd");
       HttpSession session = req.getSession(true);
       if(name.equals("wxy")&&pwd.equals("123")){
           rep.println("登录成功");

       }else {
           rep.println("登录失败");
       }
    }
}
