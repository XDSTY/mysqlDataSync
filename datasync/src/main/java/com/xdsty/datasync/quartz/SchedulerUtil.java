package com.xdsty.datasync.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class SchedulerUtil {

    @Autowired
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    private SchedulerFactoryBean schedulerFactoryBean;

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

        schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
    }

}
