package com.xdsty.datasync.xml;

import com.xdsty.datasync.DatasyncApplication;
import com.xdsty.datasync.pojo.DBInfo;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/5/12 16:37
 */
public class XmlParser {

    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

    /**
     * xml文件地址
     */
    private static final String XML_PATH = "db.xml";

    /**
     * xml文档
     */
    private static Document document;

    static {
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(new ClassPathResource(XML_PATH).getInputStream());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static List<DBInfo> getXmlDb(){
        List<DBInfo> dbInfos = new ArrayList<>(2);
        Element rootEle = document.getRootElement();
        List<Element> elements = rootEle.elements();
        elements.forEach(e -> {
            DBInfo dbInfo = new DBInfo();
            String url = e.elementText("url");
            String username = e.elementText("username");
            String password = e.elementText("password");
            if(StringUtils.isEmpty(url) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
                log.error("数据库信息不完整");
                DatasyncApplication.closeContext();
            }
            dbInfos.add(dbInfo);
        });
        return dbInfos;
    }

}
