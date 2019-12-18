package com.github.losemy.data.job;

import com.alibaba.fastjson.JSON;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.service.OrderOldService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lose
 * @date 2019-12-06
 **/
@JobHandler(value="deleteJobHandler")
@Service
@Lazy
@Slf4j
public class DeleteJobHandler extends IJobHandler {

    @Autowired
    private OrderOldService orderOldService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        try{
            for(int i=0; i < 100; i++) {
                List<OrderOldDO> orderOldDOs = orderOldService.selectByRandom();
                boolean success = orderOldService.removeByIds(orderOldDOs.stream().map(order -> order.getId()).collect(Collectors.toList()));
                XxlJobLogger.log("DeleteJob result {} {}",success, JSON.toJSONString(orderOldDOs));
            }
        }catch(Exception e){
            log.error("删除数据异常",e);
            throw e;
        }
        return SUCCESS;
    }
}
