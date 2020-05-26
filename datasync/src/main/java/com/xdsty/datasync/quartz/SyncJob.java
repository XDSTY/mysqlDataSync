package com.xdsty.datasync.quartz;

import com.xdsty.datasync.db.sync.DBSync;
import com.xdsty.datasync.pojo.SyncContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import java.sql.SQLException;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/5/25 16:11
 */
public class SyncJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(SyncJob.class);

    private DBSync dbSync;

    @Autowired
    public void setDbSync(DBSync dbSync) {
        this.dbSync = dbSync;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SyncContext syncContext = (SyncContext) jobExecutionContext.getJobDetail().getJobDataMap().get("syncContext");
        try {
            dbSync.sync(syncContext);
        } catch (SQLException | ClassNotFoundException e) {
            log.error("同步异常, ", e);
        }
    }
}
