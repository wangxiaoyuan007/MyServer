/*import servlet.Dispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;*/

/*
public class BootstrapServer {
    private  ServerSocket server;
    private  static  final  String BLANK=" ";
    private  static  final  String CRLF="\r\n";
    private  boolean isShutDown=false;
    public static void main(String[] args) throws IOException {
        BootstrapServer server=new BootstrapServer();
        server.start();
    }

    */
/**
     *
     * 启动方法
     *//*

    public void start() throws IOException{
        server = new ServerSocket(8088);
        this.receive();
    }
    */
/**
     *
     * 指定端口启动方法
     *//*

    public void start(int port) throws IOException{
        server = new ServerSocket(port);
        this.receive();
    }

    */
/**
     * 接收方法
     *//*

    public void receive() throws IOException {

        while(!isShutDown){
            Socket client= server.accept();
            new Thread(new Dispatcher(client)).start();
        }


    }

    public  void stop(){
        isShutDown=true;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
