package servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import request.Request;
import response.Response;

import java.io.*;
import result.Result;

/**
 * @auther: wxy
 * @date: 2019/5/27 22:44
 * 业务测试类
 */
public class LoginHttpServlet extends HttpServlet {
    @Override
    public void init() {

    }

    @Override
    public void doGet(Request req, Response rep) {

    }

    @Override
    public void doPost(Request req, Response rep) throws IOException {
       String name = req.getParameter("uname");
       String pwd=req.getParameter("pwd");
       //HttpSession session = req.getSession(true);
       if(name.equals("wxy")&&pwd.equals("123")){
         Result res = new Result();
         res.setCode(200);
         res.setMsg("登录成功");
         ObjectMapper mapper = new ObjectMapper();
         String json = mapper.writeValueAsString(res);
         rep.println(json);
          /* //rep.println("登录成功");

           try {
               rep.sendRedirect("web/success.html");
           } catch (IOException e) {
               e.printStackTrace();
           }*/

       }else {
           rep.println("登录失败");
       }
    }

    @Override
    public void destory(Request request, Response response) {

    }
}
