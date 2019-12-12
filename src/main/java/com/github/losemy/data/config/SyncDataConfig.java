package com.github.losemy.data.config;


import com.github.losemy.data.common.Constants;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author lose
 * @date 2019-12-06
 **/
@Configuration
public class SyncDataConfig {


    @Bean(name="syncExecutorService")
    public ExecutorService syncExecutorService(){
        //实现显示 100 跟 300 线程运行时间基本一致，瓶颈不在线程了
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("sync-data-pool-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(Constants.TOTAL_JOB, Constants.TOTAL_JOB,
                0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        return pool;
    }


    @Bean(name="checkExecutorService")
    public ExecutorService checkExecutorService(){
        //实现显示 100 跟 300 线程运行时间基本一致，瓶颈不在线程了
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("check-data-pool-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(Constants.TOTAL_JOB, Constants.TOTAL_JOB,
                0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        return pool;
    }

}
