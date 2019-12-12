package com.github.losemy.data.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.job.thread.CheckDataThread;
import com.github.losemy.data.service.OrderOldService;
import com.github.losemy.data.service.OrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * log使用 XxlJobLogger
 * @author lose
 * @date 2019-12-08
 **/
@JobHandler(value="checkDataJobHandler")
@Service
@Slf4j
@Lazy
public class CheckDataJobHandler extends IJobHandler {

    @Autowired
    private OrderOldService orderOldService;

    @Autowired
    private OrderService orderService;


    @Autowired
    @Qualifier("checkExecutorService")
    private ExecutorService executorService;


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("param {}",param);
        XxlJobLogger.log("param {}",param);
        //从哪个id开始
        // 每次任务开启都 干掉之前运行的任务
        TimeInterval timer = DateUtil.timer();
        long totalCount = 0;
        List<Future<Long>> counts = null;
        try {
            long lastId = Long.parseLong(param);
            long maxId = orderOldService.maxId() + 1;
            long begin = lastId;
            long end = 0;
            int jobs = Constants.TOTAL_JOB;
            long per = maxId / jobs;

            counts = new ArrayList<>();
            for(int i=0; i < jobs; i++){
                if(i != (jobs - 1)) {
                    end = begin + per;
                }else{
                    end = maxId;
                }
                Future<Long> count = executorService.submit(new CheckDataThread(begin,end,orderOldService,orderService));
                counts.add(count);
                begin = end;
            }

            for(int i=0; i< counts.size(); i++){
                totalCount += counts.get(i).get();
            }

            XxlJobLogger.log("SyncData-save {} data costs {}ms",totalCount,timer.intervalMs());

        }catch(Exception e){
            if (e instanceof InterruptedException) {
                // 执行中断处理
                handlerInterrupts(counts);
                throw e;
            }
            log.error("执行异常",e);
            return FAIL;
        }



        return SUCCESS;
    }

    private void handlerInterrupts(List<Future<Long>> counts){
        if(counts != null){
            counts.stream().forEach(count -> count.cancel(true));
        }
    }
}
