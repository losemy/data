package com.github.losemy.data.nacos;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author lose
 * @date 2019-12-10
 **/
@NacosPropertySource(dataId = "data", autoRefreshed = true)
@Data
@Configuration
public class ConsumeMessage {

    @NacosValue(value = "${sendMessage:false}", autoRefreshed = true)
    private boolean sendMessage;
}
