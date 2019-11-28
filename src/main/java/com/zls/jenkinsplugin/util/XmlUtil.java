package com.zls.jenkinsplugin.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;

/**
 * xml解析工具类
 * Dom4j解析xml
 *
 * @author zhangliansheng
 * @date 2019/11/3
 */
public class XmlUtil {

    public final static Element getRootElement(String xml) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File(xml));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element rootElement = document != null ? document.getRootElement() : null;
        return rootElement;
    }
}
