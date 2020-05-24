package com.xdsty.datasync.runner;

import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.SyncContext;
import com.xdsty.datasync.service.DbSyncService;
import com.xdsty.datasync.xml.XmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author 张富华
 * @date 2020/5/12 16:07
 */
@Component
public class TaskRunner implements ApplicationRunner {

    private DbSyncService syncService;

    @Autowired
    public void setSyncService(DbSyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //读取解析xml文件
        SyncContext syncContext = XmlParser.getSyncContextFromXml();
        syncService.sync(syncContext);
    }
}
