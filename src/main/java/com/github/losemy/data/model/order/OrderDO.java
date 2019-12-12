package com.github.losemy.data.model.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.losemy.data.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author lose
 * @date 2019-12-05
 **/
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order")
@Data
public class OrderDO extends BaseDO {

    @TableField(value="user_id")
    private Long userId;

    @TableField(value = "order_id")
    private Long orderId;

    @TableField(value = "remark")
    private String remark;

}
