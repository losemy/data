package com.github.losemy.data.model.demo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.losemy.data.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import javax.persistence.Column;

/**
 * @Column canal解析需要使用
 * @author lose
 * @date 2019-12-05
 **/
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order")
@Data
@Alias("OrderOldDO")
public class OrderOldDO extends BaseDO {

    @TableField(value = "user_id")
    @Column(name = "user_id")
    private Long userId;

    @TableField(value = "order_id")
    @Column(name = "order_id")
    private Long orderId;

    @TableField(value = "remark")
    @Column(name = "remark")
    private String remark;
    
}
