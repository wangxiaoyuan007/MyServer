# Web服务器实现（简化版）
___
## 简介
服务器采用NIO的模式，大大提高了性能。除了使用maven工程导入少量工具包例如
（log4j，slf4j），其余代码完全由自己实现。主要使用的是JAVA nio包下的类。
___
## 实现的功能
+ Reactor IO模型的实现
+ 对浏览器发送过来的request进行解析与封装
+ 对response进行封装，并返回给浏览器
+ Servlet相关api的实现，包括HttpServlet
+ Cookies与Session的实现
+ listenner监听器的实现（观察者模式）
+ redict重定向
___
## 整体流程
服务端主要有三个角色：Reactor，Acceptor和Pool下面是整体流程，具体详情请
查看代码。

![image](https://github.com/wangxiaoyuan007/MyServer/blob/master/src/main/resources/web/ServerProcess.png)
