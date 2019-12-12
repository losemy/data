package com.github.losemy.data.config;

import com.github.losemy.data.mq.consumer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;

/**
 * @author lose
 * @date 2019-12-10
 **/
@Configuration
public class RocketMQConsumerInitConfig implements ApplicationListener<ContextStartedEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        applicationContext.getBean(OrderAddConsumer.class);
        applicationContext.getBean(OrderDelConsumer.class);
        applicationContext.getBean(OrderUpdConsumer.class);
        applicationContext.getBean(OrderRetryDelConsumer.class);
        applicationContext.getBean(OrderRetryUpdConsumer.class);
    }
}
