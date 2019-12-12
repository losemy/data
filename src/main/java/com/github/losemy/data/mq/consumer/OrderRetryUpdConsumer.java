package com.github.losemy.data.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.mq.message.Order;
import com.github.losemy.data.service.OrderOldService;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author lose
 * @date 2019-12-10
 **/
@Lazy
@Service
@RocketMQMessageListener(topic = "${demo.rocketmq.orderTopic}",
        consumerGroup = "order4-retry-upd-consumer", selectorExpression = "retry-upd",consumeMode = ConsumeMode.CONCURRENTLY)
@Slf4j
public class OrderRetryUpdConsumer  implements RocketMQListener<Order>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderOldService orderOldService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(Order message) {
        try {
            log.info("retry-upd-message {}", JSON.toJSONString(message));
            OrderDO order = BeanMapper.map(message, OrderDO.class);
            // 可能数据没有同步过去 这里正常需要
            // 也可能数据已经更新（跑批save放进去了，此时时间戳一致，不进行更新操作）此时save 报错，数据已存在

            // 更新数据比消息旧的数据
            int result = orderService.updateByUserIdAndOrderId(order);
            log.info("handler-retry-update result {}", result);
            if(result <= 0 ){
                //发送延迟消息
                //丢弃旧的消息
                result = orderService.findByUserIdAndOrderId(order);
                if(result <= 0) {
                    OrderOldDO orderOld = BeanMapper.map(message, OrderOldDO.class);
                    result = orderOldService.findByUserIdAndOrderId(orderOld);

                    log.info("handler-retry-upd {}", result == 0 ? "already del":result);
                    if(result > 0 ) {
                        Message<Order> delayMessage = MessageBuilder.withPayload(message).build();
                        log.info("handler-retry-upd-later {}", JSON.toJSONString(message));
                        rocketMQTemplate.asyncSend(Constants.RETRY_UPD_TOPIC, delayMessage,
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
                    }
                }else{
                    log.info("handler-retry-upd-remove {}", JSON.toJSONString(message));
                }
            }
        }catch(DuplicateKeyException e){
            log.error("数据已存在 {}",JSON.toJSONString(message),e);
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
