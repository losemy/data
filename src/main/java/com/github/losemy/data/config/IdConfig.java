package com.github.losemy.data.config;


import com.github.losemy.data.util.SnowFlakeUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lose
 * @date 2019-12-01
 **/
@Configuration
public class IdConfig {

    @Bean
    public SnowFlakeUtil snowFlake(){
        //参数 配置化
        return new SnowFlakeUtil(1,2);
    }
}
