package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;


import java.io.File;
import java.net.URL;

/**
 * @auther: wxy
 * @date: 2019/5/27 23:46
 */
public class ParseXmlUtils {
    public  static Document getDocument(String path) throws DocumentException {
        SAXReader reader = new SAXReader();
        URL url = ParseXmlUtils.class.getClassLoader().getResource(path);
        File file = new File(url.getFile());
        Document document = reader.read(file);
        return  document;
    }
}
