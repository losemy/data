package com.github.losemy.data.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.service.OrderOldService;
import com.github.losemy.data.util.SnowFlakeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lose
 * @date 2019-12-06
 **/
@JobHandler(value="insertJobHandler")
@Service
@Lazy
@Slf4j
public class InsertJobHandler extends IJobHandler {

    @Autowired
    private OrderOldService orderOldService;

    @Autowired
    private SnowFlakeUtil snowFlakeUtil;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        //添加10条数据
        try{
            for(int i = 0; i < 1000; i++){
                OrderOldDO order = new OrderOldDO();
                order.setUserId(snowFlakeUtil.nextId());
                order.setOrderId(snowFlakeUtil.nextId());
                order.setRemark(RandomUtil.randomString(7));
                Date now = DateUtil.date();
                order.setCreateTime(now);
                order.setUpdateTime(now);
                boolean success = orderOldService.save(order);
                XxlJobLogger.log("InsertJob-save {}", success);
            }
        }catch(Exception e){
            log.error("插入数据异常",e);
            throw e;
        }
        return SUCCESS;
    }
}
