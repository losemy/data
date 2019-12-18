package com.github.losemy.data.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.mq.message.Order;
import com.github.losemy.data.service.OrderService;
import com.github.losemy.data.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueConsistentHash;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author lose
 * @date 2019-12-06
 **/
@Lazy
@Service
@RocketMQMessageListener(topic = "${demo.rocketmq.orderDelTopic}",
        consumerGroup = "${demo.rocketmq.orderDelTopic}", consumeMode = ConsumeMode.CONCURRENTLY)
@Slf4j
public class OrderDelConsumer implements RocketMQListener<Order>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void onMessage(Order message) {

        // 有可能数据还没有同步过来，然后删除命令来了，此时需要消息重试，而且不能立即重试
        OrderDO order = BeanMapper.map(message,OrderDO.class);
        int result = orderService.deleteByUserIdAndOrderId(order);

        log.info("del-result {} del-message {}",result,JSON.toJSONString(message));
        //messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        //level 4 对应 30s

        if(result <=0){
            //发送延迟消息
            //丢弃旧的消息
            result = orderService.findByUserIdAndOrderId(order);
            if(result <=0){
                Message<Order> delayMessage = MessageBuilder.withPayload(message).build();
                log.info("handler-delete-later {}", JSON.toJSONString(message));

                rocketMQTemplate.asyncSend(Constants.RETRY_DEL_TOPIC, delayMessage,
                        new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {
                                log.info("rocketMQ send success {}", sendResult.getMsgId());
                            }

                            @Override
                            public void onException(Throwable e) {
                                log.error("rocketMQ send error", e);
                            }
                        },
                        Constants.MESSAGE_TIMEOUT, Constants.DELAY_LEVEL);
            }else{
                log.info("handler-delete-remove {}",JSON.toJSONString(message));
            }
        }

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        //设置拉取时间间隔
        //1000ms 消费1 个消息 需要可配置 + 线程数 从offset开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setPullBatchSize(1000);
        consumer.setPullInterval(100);
        consumer.setConsumeThreadMax(20);
        consumer.setConsumeThreadMin(10);
        //只是用来处理接收是否成功？
        consumer.setMessageModel(MessageModel.CLUSTERING);
        //是否设置virtualNode cnt
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueConsistentHash());
    }
}
