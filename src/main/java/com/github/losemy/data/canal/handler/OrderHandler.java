package com.github.losemy.data.canal.handler;

import com.alibaba.fastjson.JSON;
import com.github.losemy.data.canal.model.Order;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.nacos.ConsumeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @author lose
 * @date 2019-12-06
 **/
@Component
@CanalTable(value = "simplify.t_order")
@Slf4j
public class OrderHandler implements EntryHandler<Order> {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ConsumeMessage consumeMessage;

    @Override
    public void update(Order orderBefore, Order orderAfter) {
        log.info("handler-update before {} , after {}", JSON.toJSONString(orderBefore),JSON.toJSONString(orderAfter));

        if(consumeMessage.isSendMessage()) {
            rocketMQTemplate.asyncSend(Constants.UPD_TOPIC, orderAfter,
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("rocketMQ send update success {}", sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("rocketMQ send error", e);
                        }
                    },
            Constants.MESSAGE_TIMEOUT);
        }

    }

    @Override
    public void insert(Order orderOld) {
        log.info("handler-insert {}", JSON.toJSONString(orderOld));
        if(consumeMessage.isSendMessage()) {
            rocketMQTemplate.asyncSend(Constants.ADD_TOPIC, orderOld,
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("rocketMQ send add success {}", sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("rocketMQ send error", e);
                        }
                    },
            Constants.MESSAGE_TIMEOUT);
        }
    }


    @Override
    public void delete(Order orderOld) {
        log.info("handler-delete {}", JSON.toJSONString(orderOld));
        if(consumeMessage.isSendMessage()) {
            rocketMQTemplate.asyncSend(Constants.DEL_TOPIC, orderOld,
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("rocketMQ send delete success {}", sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("rocketMQ send error", e);
                        }
                    },
            Constants.MESSAGE_TIMEOUT);
        }

    }


}
