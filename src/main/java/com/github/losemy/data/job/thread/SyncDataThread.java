package com.github.losemy.data.job.thread;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSON;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.service.OrderOldService;
import com.github.losemy.data.service.OrderService;
import com.github.losemy.data.util.BeanMapper;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 应该只需要考虑save操作
 * 其他操作不需要理会
 * @author lose
 * @date 2019-12-06
 **/
@Slf4j
public class SyncDataThread implements Callable<Long> {

    private long begin;
    private long end;
    private OrderOldService orderOldService;
    private OrderService orderService;

    public SyncDataThread(long begin, long end, OrderOldService orderOldService, OrderService orderService) {
        this.begin = begin;
        this.end = end;
        this.orderOldService = orderOldService;
        this.orderService = orderService;
    }

    /**
     * [2500, 5000)
     * [0, 2500)
     * limit 1000
     *
     * @return
     * @throws Exception
     */
    @Override
    public Long call() throws Exception {
        XxlJobLogger.log("id ranges [{}, {})",begin,end);
        long count = 0;
        TimeInterval timer = DateUtil.timer();
        TimeInterval cycleTimer = DateUtil.timer();
        try{
            long lastId = begin;
            while(!Thread.currentThread().isInterrupted()){
                List<OrderOldDO> orderOldDOS = orderOldService.findByPageAndMaxId(Constants.PAGE_SIZE, lastId,end);
                XxlJobLogger.log("orderOldDOS {}", orderOldDOS.size());
                if (CollUtil.isEmpty(orderOldDOS)) {
                    log.info("当前任务执行完毕");
                    break;
                }

                // 额外使用 是否会有提升 需要测试啊
                // 测试表明提升不大 甚至没有
                long paCount = orderOldDOS.stream()
                        .filter(orderOldDO -> orderService.findCount(orderOldDO.getUserId(),orderOldDO.getOrderId()) <= 0)
                        .mapToLong(orderOldDO -> {
                            try {
                                OrderDO orderDO = BeanMapper.map(orderOldDO, OrderDO.class);
                                orderDO.setId(null);
                                boolean success = orderService.save(orderDO);
                                XxlJobLogger.log("SyncData-save {} success {}", JSON.toJSONString(orderOldDO),success);
                            }catch(DuplicateKeyException e){
                                XxlJobLogger.log("save失败");
                                log.error("save失败-数据已存在",e);
                                return 0;
                            }
                            return 1;
                        }).sum();

                lastId = orderOldDOS.get(orderOldDOS.size()-1).getId() + 1;
                count += paCount;
                XxlJobLogger.log("SyncData-save {} data costs {}ms",paCount,cycleTimer.intervalMs());
                cycleTimer.restart();
            }
            XxlJobLogger.log("SyncData-save {} data costs {}ms",count,timer.intervalMs());
        }catch(Exception e){
            log.error("同步数据异常", e);
        }

        return count;
    }
}
