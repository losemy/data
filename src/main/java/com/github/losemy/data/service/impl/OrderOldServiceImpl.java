package com.github.losemy.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.losemy.data.dao.demo.OrderOldDao;
import com.github.losemy.data.model.demo.OrderOldDO;
import com.github.losemy.data.service.OrderOldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author lose
 * @since 2019-12-09
 */
@Service
@Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
public class OrderOldServiceImpl extends ServiceImpl<OrderOldDao, OrderOldDO> implements OrderOldService {

    @Autowired
    private OrderOldDao orderOldDao;


    @Override
    public List<OrderOldDO> findByPage(int pageSize, long lastId) {
        return orderOldDao.findByPage(pageSize, lastId);
    }

    @Override
    public List<OrderOldDO> findByPageAndMaxId(int pageSize, long lastId, long maxId) {
        return orderOldDao.findByPageAndMaxId(pageSize, lastId,maxId);
    }

    @Override
    public List<OrderOldDO> selectByRandom() {
        return orderOldDao.selectByRandom();
    }

    @Override
    public long maxId() {
        return orderOldDao.maxId();
    }

    @Override
    public int findByUserIdAndOrderId(OrderOldDO orderOld) {
        return orderOldDao.findByUserIdAndOrderId(orderOld);
    }
}
