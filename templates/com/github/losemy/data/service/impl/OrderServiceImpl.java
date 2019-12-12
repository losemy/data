package com.github.losemy.data.service.impl;

import com.github.losemy.data.entity.Order;
import com.github.losemy.data.mapper.OrderMapper;
import com.github.losemy.data.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Spring Boot Demo 分库分表 系列示例表0 服务实现类
 * </p>
 *
 * @author lose
 * @since 2019-12-12
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
