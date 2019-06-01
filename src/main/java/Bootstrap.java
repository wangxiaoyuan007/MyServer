import lombok.extern.slf4j.Slf4j;
import network.Reactor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;
/**
 * @auther: wxy
 * @date: 2019/5/29 12:42
 */
@Slf4j
public class Bootstrap {

    public static void main(String[] args) throws IOException {

        Thread server = new Thread(new Reactor(getPort()));
        log.info("服务器启动！");
        server.start();
    }

    public static int  getPort(){
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = Bootstrap.class.getClassLoader().getResourceAsStream("application.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取key对应的value值
        String port = properties.getProperty("port");
        return Integer.valueOf(port);
    }
}
