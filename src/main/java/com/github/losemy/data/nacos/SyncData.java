package com.github.losemy.data.nacos;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author lose
 * @date 2019-12-11
 **/
@Data
@Configuration
public class SyncData implements Serializable{

    private static final long serialVersionUID = 1L;

    private long maxId = 0;
    private boolean syncDataFinish = false;

}
