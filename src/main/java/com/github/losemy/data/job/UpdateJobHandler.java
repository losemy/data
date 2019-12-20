package com.github.losemy.data.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.service.OrderOldService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时更新数据，让canal接收模拟
 * @author lose
 * @date 2019-12-05
 **/
@JobHandler(value="updateJobHandler")
@Service
@Slf4j
public class UpdateJobHandler extends IJobHandler {

    @Autowired
    private OrderOldService orderOldService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {

        try {
            int length = 6;
            if(StrUtil.isNotEmpty(param)){
                length = Integer.parseInt(param);
            }
            for(int i=0; i < 100; i++) {
                //这种方式可能会导致获取数据重复
                List<OrderOldDO> orderOldDOs = orderOldService.selectByRandom();
                for (OrderOldDO orderOldDO : orderOldDOs) {
                    orderOldDO.setRemark(RandomUtil.randomString(length));
                    orderOldDO.setUpdateTime(DateUtil.date());
                }

                boolean success = orderOldService.updateBatchById(orderOldDOs);
                XxlJobLogger.log("UpdateJob-update {}",success);
            }
        }catch(Exception e){
            XxlJobLogger.log(e);
            log.error("UpdateJobHandler failed",e);
            throw e;
        }
        return SUCCESS;
    }
}
