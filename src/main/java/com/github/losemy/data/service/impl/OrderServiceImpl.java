package com.github.losemy.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.losemy.data.dao.order.OrderDao;
import com.github.losemy.data.model.order.OrderDO;
import com.github.losemy.data.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * @author lose
 * @since 2019-12-09
 */
@Service
@Transactional(transactionManager = "shardingTransactionManager",rollbackFor = Exception.class)
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderDO> implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public OrderDO findByUserIdAndOrderIdAndUpdateTime(Long userId, Long orderId, Date updateTime) {
        return orderDao.findByUserIdAndOrderIdAndUpdateTime(userId, orderId, updateTime);
    }

    @Override
    public int updateByUserIdAndOrderId(OrderDO order) {
        return orderDao.updateByUserIdAndOrderId(order);
    }

    @Override
    public int deleteByUserIdAndOrderId(OrderDO order) {
        return orderDao.deleteByUserIdAndOrderId(order);
    }

    @Override
    public int findByUserIdAndOrderId(OrderDO order) {
        return orderDao.findByUserIdAndOrderId(order);
    }

    @Override
    public int findCount(Long userId, Long orderId) {
        return orderDao.findCount(userId,orderId);
    }
}
