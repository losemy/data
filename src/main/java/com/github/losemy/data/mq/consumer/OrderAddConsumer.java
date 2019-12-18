package com.github.losemy.data.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.mq.message.Order;
import com.github.losemy.data.service.OrderService;
import com.github.losemy.data.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueConsistentHash;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @author lose
 * @date 2019-12-06
 **/
@Lazy
@Service
@RocketMQMessageListener(topic = "${demo.rocketmq.orderAddTopic}",
        consumerGroup = "order", consumeMode = ConsumeMode.CONCURRENTLY)
@Slf4j
public class OrderAddConsumer implements RocketMQListener<Order>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(Order message) {
        log.info("add-message {}", JSON.toJSONString(message));
        try {
            OrderDO order = BeanMapper.map(message, OrderDO.class);
            order.setId(null);

            int result = orderService.findCount(order.getUserId(),order.getOrderId());

            if(result <=0 ) {
                log.info("order consumer {}", JSON.toJSONString(order));
                boolean success = orderService.save(order);
                log.info("handler-insert after {}", success);
            }
        }catch(DuplicateKeyException e){
            //正常情况下 主键不该有冲突的，然而多机情况下，如果snowFlow不修改就可能出现了
            log.error("数据已存在 {}", JSON.toJSONString(message));
        }
    }

    /**
     * 有被限速但是限速规则 是针对的线程
     * 也就是实际数量会 * 当前线程数？（为什么不是固定的20）
     * @param consumer
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        //设置拉取时间间隔
        //1000ms 消费1 个消息 需要可配置 + 线程数 从offset开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setPullBatchSize(1000);
        consumer.setPullInterval(100);
        consumer.setConsumeThreadMax(40);
        consumer.setConsumeThreadMin(20);
        //只是用来处理接收是否成功？
        consumer.setMessageModel(MessageModel.CLUSTERING);
        //是否设置virtualNode cnt
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueConsistentHash());

    }
}
