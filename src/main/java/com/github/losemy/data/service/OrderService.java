package com.github.losemy.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.losemy.data.model.order.OrderDO;

import java.util.Date;

/**
 * @author lose
 * @date 2019-12-09
 **/
public interface OrderService extends IService<OrderDO> {
    /**
     * 通过userId 以及 orderId查询数据
     * @param userId
     * @param orderId
     * @param updateTime
     * @return
     */
    OrderDO findByUserIdAndOrderIdAndUpdateTime(Long userId,Long orderId, Date updateTime);

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

    /**
     * 查询是否存在同步数据落后实际数据的情况
     * @param order
     * @return
     */
    int findByUserIdAndOrderId(OrderDO order);

    int findCount(Long userId, Long orderId);
}
