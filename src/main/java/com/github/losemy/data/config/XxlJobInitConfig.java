package com.github.losemy.data.config;


import com.github.losemy.data.job.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @see ApplicationReadyEvent
 * @see org.springframework.boot.context.event.ApplicationStartedEvent
 * @author lose
 * @date 2019-12-10
 **/
@Configuration
public class XxlJobInitConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        applicationContext.getBean(CheckDataJobHandler.class);
        applicationContext.getBean(DeleteJobHandler.class);
        applicationContext.getBean(InsertJobHandler.class);
        applicationContext.getBean(SyncDataJobHandler.class);
        applicationContext.getBean(UpdateJobHandler.class);
    }
}
