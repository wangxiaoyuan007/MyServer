package utils;

import java.io.*;
import java.net.URL;

/**
 * @auther: wxy
 * @date: 2019/6/2 15:24
 */
public class PaseHtmlUtil {
    public static byte[] getHtml(String url) throws IOException {

        URL url1 = PaseHtmlUtil.class.getClassLoader().getResource(url);
        File file = new File(url1.getFile());
        InputStream in = new FileInputStream(file);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        return  bytes;

    }
}
