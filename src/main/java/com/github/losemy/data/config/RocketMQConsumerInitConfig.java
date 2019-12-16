package com.github.losemy.data.config;

import com.github.losemy.data.mq.consumer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟加载，避免项目没有启动就开始消费，导致一些异常
 * @author lose
 * @date 2019-12-10
 **/
@Configuration
public class RocketMQConsumerInitConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        applicationContext.getBean(OrderAddConsumer.class);
        applicationContext.getBean(OrderDelConsumer.class);
        applicationContext.getBean(OrderUpdConsumer.class);
        applicationContext.getBean(OrderRetryDelConsumer.class);
        applicationContext.getBean(OrderRetryUpdConsumer.class);
    }
}
