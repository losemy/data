package com.github.losemy.data.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.github.losemy.data.common.Constants;
import com.github.losemy.data.job.thread.SyncDataThread;
import com.github.losemy.data.nacos.SyncData;
import com.github.losemy.data.service.OrderOldService;
import com.github.losemy.data.service.OrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author lose
 * @date 2019-09-10
 **/
@JobHandler(value="syncDataJobHandler")
@Service
@Slf4j
public class SyncDataJobHandler extends IJobHandler {

    @Autowired
    private OrderOldService orderOldService;

    @Autowired
    private OrderService orderService;


    @Autowired
    @Qualifier("syncExecutorService")
    private ExecutorService executorService;


    @Autowired
    private SyncData syncData;

    @Override
    public ReturnT<String> execute(String param) throws Exception {

        log.info("param {}",param);
        //这个 param需要调整 ，不然每次都是全量 很浪费时间
        XxlJobLogger.log("param {}",param);
        //从哪个id开始
        // 每次任务开启都 干掉之前运行的任务

        TimeInterval timer = DateUtil.timer();
        long totalCount = 0;
        syncData.setSyncDataFinish(false);
        syncData.setMaxId(0);
        long maxId;
        List<Future<Long>> counts = null;
        try {
            long lastId = Long.parseLong(param);
            maxId = orderOldService.maxId() + 1;
            XxlJobLogger.log("SyncData-save maxId {}",maxId-1);
            long begin = lastId;
            long end = 0;
            int jobs = Constants.TOTAL_JOB;
            long per = (maxId - lastId) / jobs;

            counts = new ArrayList<>();
            for(int i=0; i < jobs; i++){
                if(i != (jobs - 1)) {
                    end = begin + per;
                }else{
                    end = maxId;
                }
                Future<Long> count = executorService.submit(new SyncDataThread(begin,end,orderOldService,orderService));
                counts.add(count);
                begin = end;
            }

            for(int i=0; i< counts.size(); i++){
                totalCount += counts.get(i).get();
            }


            syncData.setSyncDataFinish(true);
            syncData.setMaxId(maxId);


            //todo 待解决bug InheritableThreadLocal结合线程池使用释放的问题
            // 使用线程池 壳只会被创建一次也就是后续都是那个壳 不会变
            // TransmittableThreadLocal https://github.com/alibaba/transmittable-thread-local
            // -javaagent:/apps/jars/transmittable-thread-local-2.11.2.jar
            log.info("SyncData-save {} data costs {}ms",totalCount,timer.intervalMs());
            XxlJobLogger.log("SyncData-save {} data costs {}ms",totalCount,timer.intervalMs());

        }catch(Exception e){
            if (e instanceof InterruptedException) {
                // 执行中断处理
                handlerInterrupts(counts);
            }
            log.error("执行异常",e);
            throw e;
        }

        return SUCCESS;
    }

    private void handlerInterrupts(List<Future<Long>> counts){
        if(counts != null){
            counts.stream().forEach(count -> count.cancel(true));
        }
    }

}
