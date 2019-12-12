package com.github.losemy.data.mq.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lose
 * @date 2019-12-06
 **/
@Data
public class Order implements Serializable{
    private Long id;

    private Long userId;

    private Long orderId;

    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次更新时间
     */
    private Date updateTime;

}
