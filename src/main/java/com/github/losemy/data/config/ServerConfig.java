package com.github.losemy.data.config;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lose
 * @date 2019-12-02
 **/
@Configuration
@Slf4j
public class ServerConfig {

    @Value("${server.port}")
    private int port;

    /**
     * 不修改配置的情况下启动多个服务
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                while(!NetUtil.isUsableLocalPort(port)) {
                    port = RandomUtil.randomInt(8080,20880);
                }
                log.info("start-port {}",port);
                factory.setPort(port);
            }
        };
    }

}
