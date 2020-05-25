package com.xdsty.datasync.quartz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.Job;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerJob {

    private Class<? extends Job> jobClazz;

    private Map<String, Object> params;

    private String jobKey;

    private String jobGroup;

    private String triggerKey;

    private String triggerGroup;

    private String cron;
}
