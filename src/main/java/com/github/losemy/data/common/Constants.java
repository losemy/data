package com.github.losemy.data.common;

/**
 * @author lose
 * @date 2019-11-18
 **/
public interface Constants {

    String SHORT_URL_PREFIX = "http://t.tn/shortUrl/";

    String REDIS_SHORT_URL_PREFIX = "short-url:";

    String REDIS_TARGET_URL_PREFIX = "target-url:";

    int PAGE_SIZE = 1000;

    int TOTAL_JOB = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 对应rocketMQ 30s
     */
    int DELAY_LEVEL = 9;

    int MESSAGE_TIMEOUT = 50000;


    /**
     * 消息topic
     */
    String ADD_TOPIC = "add-order";
    String DEL_TOPIC = "del-order";
    String UPD_TOPIC = "upd-order";
    /**
     * 重试消息队列 topic
     */
    String RETRY_DEL_TOPIC = "retry-del-order";
    String RETRY_UPD_TOPIC = "retry-upd-order";
}
