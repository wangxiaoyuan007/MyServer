package Handler;

import listenner.ServletContextListener;
import mapping.ServerletMapping;
import mapping.UrlMapping;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import utils.ParseXmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther: wxy
 * @date: 2019/5/27 23:59
 * 解析web.xml
 */
public class ServerletHandler {
    private Map<String,ServerletMapping> serverlets;
    private Map<String, UrlMapping> patterns;
    private  List<String> listeners;
    private Document document;


    public ServerletHandler() {
        this.serverlets=new HashMap<>();
        this.patterns=new HashMap<>();
        this.listeners = new ArrayList<>();
       inits();
    }

    private void inits() {
        try {
            document= ParseXmlUtils.getDocument("web.xml");
            Element rootElement=document.getRootElement();
            Element serverletsElement=rootElement.element("serverlets");

            List<Element> serverlets_element = serverletsElement.elements("servlet");
            List<Element> serverletsMappingElement = rootElement.elements("servlet-mapping");
            List<Element> listenners_element = rootElement.elements("listener");
            //解析serverlets
            for(Element e:serverlets_element){
                ServerletMapping serverletMapping = new ServerletMapping();
                Element nameElement = e.element("name");
                Element classElement = e.element("servlet-name");
                serverletMapping.setName(nameElement.getText());
                serverletMapping.setServerletName(classElement.getText());
                serverlets.put(serverletMapping.getName(),serverletMapping);
            }

            //解析serverlet-mapping
            for(Element e:serverletsMappingElement){
                UrlMapping urlMapping = new UrlMapping();
                Element urlPatternElement = e.element("url-pattern");
                Element serverletNameElement = e.element("servlet-name");
                urlMapping.setServerletName(serverletNameElement.getText());
                urlMapping.setUrlPattern(urlPatternElement.getText());
                this.patterns.put(urlMapping.getUrlPattern(),urlMapping);
            }

            //解析listenner
            for(Element e:listenners_element){
                Element listenerClassElement = e.element("listener-class");
                listeners.add(listenerClassElement.getText().trim());
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    public Map<String, ServerletMapping> getServerlets() {
        return serverlets;
    }

    public Map<String, UrlMapping> getPatterns() {
        return patterns;
    }

    public List<String> getListeners() {
        return listeners;
    }
}
