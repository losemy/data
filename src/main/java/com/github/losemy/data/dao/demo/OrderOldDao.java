package com.github.losemy.data.dao.demo;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.losemy.data.model.demo.OrderOldDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lose
 * @date 2019-12-05
 **/
public interface OrderOldDao extends BaseMapper<OrderOldDO> {
    /**
     * 分页获取数据
     * @param pageSize
     * @param lastId
     * @return
     */
    List<OrderOldDO> findByPage(@Param("pageSize") int pageSize, @Param("lastId") long lastId);


    /**
     * 分页获取数据
     * @param pageSize
     * @param lastId
     * @param maxId
     * @return
     */
    List<OrderOldDO> findByPageAndMaxId(@Param("pageSize") int pageSize, @Param("lastId") long lastId, @Param("maxId") long maxId);

    /**
     * 随机获取数据
     * @return
     */
    List<OrderOldDO> selectByRandom();

    /**
     * 获取最大id
     * @return
     */
    long maxId();

    /**
     * 数据是否在老库中存在
     * @param orderOld
     * @return
     */
    int findByUserIdAndOrderId(OrderOldDO orderOld);
}
