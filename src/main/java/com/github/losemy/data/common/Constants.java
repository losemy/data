package com.github.losemy.data.common;

/**
 * @author lose
 * @date 2019-11-18
 **/
public interface Constants {

    String SHORT_URL_PREFIX = "http://t.tn/shortUrl/";

    String REDIS_SHORT_URL_PREFIX = "short-url:";

    String REDIS_TARGET_URL_PREFIX = "target-url:";

    int PAGE_SIZE = 100;

    int TOTAL_JOB = 30;

    /**
     * 对应rocketMQ 30s
     */
    int DELAY_LEVEL = 9;

    int MESSAGE_TIMEOUT = 50000;

    String TOPIC = "order4";
    String ADD_TOPIC = TOPIC +":add";

    String DEL_TOPIC = TOPIC + ":del";

    String UPD_TOPIC = TOPIC + ":upd";

    String RETRY_DEL_TOPIC = TOPIC + ":retry-del";
    String RETRY_UPD_TOPIC = TOPIC + ":retry-upd";
}
