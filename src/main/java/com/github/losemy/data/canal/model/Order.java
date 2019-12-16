package com.github.losemy.data.canal.model;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author lose
 * @date 2019-12-13
 **/
@Data
public class Order {

    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "remark")
    private String remark;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 上次更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
