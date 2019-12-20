package com.github.losemy.data.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.losemy.data.DataApplication;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.mq.message.Order;
import com.github.losemy.data.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;

/**
 * @author lose
 * @date 2019-12-09
 **/
@SpringBootTest(classes = {DataApplication.class})
@RunWith(SpringRunner.class)
@Slf4j
public class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @NacosInjected
    private ConfigService configService;


    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;


    @Test
    public void testRedis(){
        Order o = new Order();
        o.setId(123L);
        o.setRemark(RandomUtil.randomString(6));
        redisTemplate.opsForValue().set(o.getRemark(),o);
        log.info("{}",redisTemplate.opsForValue().get(o.getRemark()));
    }

    @Test
    public void testNacos() throws NacosException {
        String content = "maxId="+100 + "&syncDataFinish=true";
        configService.publishConfig("data",DEFAULT_GROUP,content);
    }

    @Test
    public void testDelete(){
        OrderDO order = JSON.parseObject(" {\"createTime\":1575989937000,\"id\":1582,\"orderId\":80,\"remark\":\"eldev8\",\"updateTime\":1575991347000,\"userId\":80}",OrderDO.class);
        int result = orderService.deleteByUserIdAndOrderId(order);
        Assert.assertEquals(1,result);
    }

    @Test
    public void findByUserIdAndOrderIdAndUpdateTime() {
        OrderDO order = new OrderDO();
        order.setUserId(22512L);
        order.setOrderId(67536L);
        order.setId(1L);
        order.setRemark("123");
        try {
            orderService.save(order);
        }catch(Exception e){
            log.error("数据冲突",e);
        }
    }

    @Test
    public void updateByUserIdAndOrderId() {
        OrderDO order = JSON.parseObject("{\"createTime\":1575894383000,\"id\":12,\"orderId\":12,\"remark\":\"cer9uf\",\"updateTime\":1575894442000,\"userId\":1}",OrderDO.class);
        //正常来说id需要set null 但是sql不需要使用
        log.info("{}",orderService.findByUserIdAndOrderId(order));
        int result = orderService.updateByUserIdAndOrderId(order);
        Assert.assertEquals(1,result);
    }

    @Test
    public void deleteByUserIdAndOrderId() {
    }
}