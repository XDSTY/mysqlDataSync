package com.xdsty.datasync.quartz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/5/25 17:46
 */
@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(SpringBeanJobFactory jobFactory) throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);

        Properties properties = new Properties();
        InputStream in = QuartzConfig.class.getClassLoader().getResourceAsStream("quartz.properties");
        properties.load(in);

        schedulerFactoryBean.setQuartzProperties(properties);
        return schedulerFactoryBean;
    }

}
