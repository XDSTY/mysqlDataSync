package com.xdsty.datasync.runner;

import com.xdsty.datasync.pojo.SyncContext;
import com.xdsty.datasync.quartz.SchedulerJob;
import com.xdsty.datasync.quartz.SchedulerUtil;
import com.xdsty.datasync.quartz.SyncJob;
import com.xdsty.datasync.service.DbSyncService;
import com.xdsty.datasync.xml.XmlParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张富华
 * @date 2020/5/12 16:07
 */
@Component
public class TaskRunner implements ApplicationRunner {

    private SchedulerUtil schedulerUtil;

    private DbSyncService syncService;

    @Autowired
    public void setSchedulerUtil(SchedulerUtil schedulerUtil) {
        this.schedulerUtil = schedulerUtil;
    }

    @Autowired
    public void setSyncService(DbSyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //读取解析xml文件
        SyncContext syncContext = XmlParser.getSyncContextFromXml();
//        syncService.sync(syncContext);
        //注册定时任务
        if(StringUtils.isEmpty(syncContext.getCorn())){
            syncService.sync(syncContext);
        } else {
            SchedulerJob schedulerJob = new SchedulerJob();
            schedulerJob.setCron(syncContext.getCorn());
            schedulerJob.setJobClazz(SyncJob.class);
            schedulerJob.setJobKey("jobKey");
            schedulerJob.setJobGroup("jobGroup");
            schedulerJob.setTriggerKey("triggerKey");
            schedulerJob.setTriggerGroup("triggerGroup");
            Map<String, Object> map = new HashMap<>();
            map.put("syncContext", syncContext);
            schedulerJob.setParams(map);
            schedulerUtil.addJob(schedulerJob);
        }
        
    }
}
