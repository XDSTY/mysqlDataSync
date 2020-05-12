package com.xdsty.datasync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DatasyncApplication {

    private static final Logger log = LoggerFactory.getLogger(DatasyncApplication.class);

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(DatasyncApplication.class, args);
    }

    /**
     * 关闭程序
     */
    public static void closeContext(){
        log.error("关闭程序");
        context.close();
    }

}
