package com.xdsty.datasync.xml;

import com.xdsty.datasync.DatasyncApplication;
import com.xdsty.datasync.enums.DbTypeEnum;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.DataSyncInfo;
import com.xdsty.datasync.pojo.SyncContext;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author 张富华
 * @date 2020/5/12 16:37
 */
public class XmlParser {

    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

    /**
     * xml文件地址
     */
    private static final String XML_PATH = "db.xml";

    private static final String FROM_DB = "from.db";
    private static final String DEST_DB = "dest.db";
    private static final String CORN = "corn";
    private static final String EMAIL = "email";
    private static final String DATA_SYNC = "data.sync";

    /**
     * xml文档
     */
    private static Document document;

    public static SyncContext getSyncContextFromXml() {
        SAXReader saxReader = new SAXReader();
        InputStream file = null;
        try {
             file = new FileInputStream(XML_PATH);
        } catch (FileNotFoundException e) {
            log.error("找不到自定义的db.xml文件，开始寻找资源目录下的db.xml文件");
            try {
                file = new ClassPathResource(XML_PATH).getInputStream();
                log.error("使用资源目录下的db.xml文件");
            } catch (IOException e1) {
                log.error("找不到db.xml文件，停止项目");
                DatasyncApplication.closeContext();
            }
        }
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            log.error("读取配置文件失败");
            DatasyncApplication.closeContext();
        }

        SyncContext syncContext = new SyncContext();
        Element rootEle = document.getRootElement();
        List<Element> elements = rootEle.elements();
        elements.forEach(e -> {
            if(FROM_DB.equals(e.getName())){
                syncContext.setFromDb(handleDbElement(e));
            } else if (DEST_DB.equals(e.getName())) {
                syncContext.setDestDb(handleDbElement(e));
            } else if(CORN.equals(e.getName())) {
                syncContext.setCorn(e.getStringValue());
            } else if(EMAIL.equals(e.getName())) {
                syncContext.setEmail(e.getStringValue());
            } else if(DATA_SYNC.equals(e.getName())) {
                syncContext.setDataSync(handleDataSyncElement(e));
            }
        });
        return syncContext;
    }

    private static DBInfo handleDbElement(Element e) {
        DBInfo dbInfo = new DBInfo();
        String url = e.elementText("url");
        String username = e.elementText("username");
        String password = e.elementText("password");
        String dbType = e.elementText("db.type");
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(dbType)) {
            log.error("数据库信息不完整");
            DatasyncApplication.closeContext();
        }
        dbInfo.setUrl("jdbc:mysql://" + url);
        dbInfo.setUsername(username);
        dbInfo.setPassword(password);
        dbInfo.setDbType(dbType);
        dbInfo.setDriver(DbTypeEnum.getDriverByType(dbType));
        return dbInfo;
    }

    private static DataSyncInfo handleDataSyncElement(Element e) {
        DataSyncInfo dataSync = new DataSyncInfo();
        String flag = e.elementText("flag");
        Long limit = StringUtils.isNotEmpty(e.elementText("limit")) ? Long.parseLong(e.elementText("limit")) : 3000L;
        dataSync.setFlag(flag);
        dataSync.setLimit(limit);
        return dataSync;
    }

}
