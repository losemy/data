package com.github.losemy.data.job.thread;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.service.OrderOldService;
import com.github.losemy.data.service.OrderService;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author lose
 * @date 2019-12-08
 **/
@Slf4j
public class CheckDataThread implements Callable<Long> {

    private long begin;
    private long end;
    private OrderOldService orderOldService;
    private OrderService orderService;

    public CheckDataThread(long begin, long end, OrderOldService orderOldService, OrderService orderService) {
        this.begin = begin;
        this.end = end;
        this.orderOldService = orderOldService;
        this.orderService = orderService;
    }
    @Override
    public Long call() throws Exception {
        log.info("check id ranges [{}, {})",begin,end);
        long count = 0;
        TimeInterval timer = DateUtil.timer();
        try{
            long lastId = begin;
            while(!Thread.currentThread().isInterrupted()){
                List<OrderOldDO> orderOldDOS = orderOldService.findByPageAndMaxId(Constants.PAGE_SIZE, lastId,end);
                log.info("orderOldDOS {}", orderOldDOS.size());
                if (CollUtil.isEmpty(orderOldDOS)) {
                    log.info("当前任务执行完毕");
                    break;
                }

                long paCount = orderOldDOS.stream()
                        .filter(orderOldDO -> orderService.findCount(orderOldDO.getUserId(),orderOldDO.getOrderId()) <= 0)
                        .count();
                lastId = orderOldDOS.get(orderOldDOS.size()-1).getId() + 1;
                count += paCount;
            }
            log.info("Check-Data not-sync {} data costs {}ms",count,timer.intervalMs());
            XxlJobLogger.log("Check-Data not-sync {} data costs {}ms",count,timer.intervalMs());
        }catch(Exception e){
            log.error("同步数据异常", e);
        }

        return count;
    }
}
