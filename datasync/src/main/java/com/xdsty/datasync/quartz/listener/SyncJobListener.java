package com.xdsty.datasync.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/5/26 9:49
 */
@Component
public class SyncJobListener implements JobListener {

    private static final Logger log = LoggerFactory.getLogger(SyncJobListener.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.error(this.getName() + " jobToBeExecuted");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.error(this.getName() + " jobExecutionVetoed");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.error(this.getName() + " jobWasExecuted");
    }
}
