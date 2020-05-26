package com.xdsty.datasync.quartz;

import com.xdsty.datasync.quartz.listener.SyncJobListener;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class SchedulerUtil {

    @Autowired
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @Autowired
    public void setSyncJobListener(SyncJobListener syncJobListener) {
        this.syncJobListener = syncJobListener;
    }

    private SchedulerFactoryBean schedulerFactoryBean;

    private SyncJobListener syncJobListener;

    public void addJob(SchedulerJob schedulerJob) throws SchedulerException {
        JobBuilder jobBuilder = JobBuilder.newJob(schedulerJob.getJobClazz())
                .withIdentity(schedulerJob.getJobKey(), schedulerJob.getJobGroup());
        JobDataMap map = new JobDataMap(schedulerJob.getParams());
        schedulerJob.getParams().forEach((String k, Object v) -> {
            jobBuilder.usingJobData(map);
        });
        JobDetail jobDetail = jobBuilder.build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(schedulerJob.getTriggerKey(), schedulerJob.getTriggerGroup())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(schedulerJob.getCron()))
                .build();

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.getListenerManager().addJobListener(syncJobListener, KeyMatcher.keyEquals(JobKey.jobKey(schedulerJob.getJobKey())));
    }

}
