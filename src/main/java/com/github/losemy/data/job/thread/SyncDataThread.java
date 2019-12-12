package com.github.losemy.data.job.thread;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.service.OrderOldService;
import com.github.losemy.data.service.OrderService;
import com.github.losemy.data.util.BeanMapper;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.Iterator;
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
        log.info("id ranges [{}, {})",begin,end);
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
                Iterator<OrderOldDO> orderOldDOIterator = orderOldDOS.iterator();
                while (orderOldDOIterator.hasNext()) {
                    OrderOldDO orderOldDO = orderOldDOIterator.next();
                    orderOldDOIterator.remove();
                    lastId = orderOldDO.getId();
                    //需要验证数据新旧决定是否需要save 需要考虑覆盖
                    OrderDO orderDO = orderService.findByUserIdAndOrderIdAndUpdateTime(orderOldDO.getUserId(),orderOldDO.getOrderId(),orderOldDO.getUpdateTime());

                    int result = orderService.findCount(orderOldDO.getUserId(),orderOldDO.getOrderId());

                    if(result <= 0){
                        // save 默认会使用ID回查 所以 bug出现了
                        count++;
                        try {
                            orderService.save(BeanMapper.map(orderOldDO, OrderDO.class));
                            continue;
                        }catch(DuplicateKeyException e){
                            log.error("save失败");
                            count--;
                        }

                    }
                }
                lastId++;
                XxlJobLogger.log("SyncData-save {} data costs {}ms",count,timer.intervalMs());
            }
            log.info("SyncData-save {} data costs {}ms",count,timer.intervalMs());
            XxlJobLogger.log("SyncData-save {} data costs {}ms",count,timer.intervalMs());
        }catch(Exception e){
            log.error("同步数据异常", e);
        }

        return count;
    }
}
