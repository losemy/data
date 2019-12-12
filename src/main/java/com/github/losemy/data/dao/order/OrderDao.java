package com.github.losemy.data.dao.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.losemy.data.model.order.OrderDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author lose
 * @date 2019-12-05
 **/
public interface OrderDao extends BaseMapper<OrderDO> {

    /**
     * 通过userId 以及 orderId查询数据
     * @param userId
     * @param orderId
     * @param updateTime
     * @return
     */
    OrderDO findByUserIdAndOrderIdAndUpdateTime(@Param("userId") Long userId,@Param("orderId") Long orderId,@Param("updateTime") Date updateTime);

    /**
     * 通过userId 以及 orderId更新数据 需要保证时间在之前
     * @param order
     * @return
     */
    int updateByUserIdAndOrderId(OrderDO order);

    /**
     * 通过userId 以及 orderId删除数据 需要保证时间在之前
     * sharding-jdbc delete不支持别名
     * delete 不返回影响行数
     * 按照时间删除，不满足时间的不予删除
     * @param order
     * @return
     */
    int deleteByUserIdAndOrderId(OrderDO order);

    int findByUserIdAndOrderId(OrderDO order);

    int findCount(@Param("userId") Long userId,@Param("orderId") Long orderId);
}
