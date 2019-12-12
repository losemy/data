package com.github.losemy.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lose
 * @date 2019-11-18
 * 使用@EntityListeners  @CreatedDate、@LastModifiedDate 才能生效
 **/
@Data
public class BaseDO implements Serializable {

    private static final long serialVersionUID = -7998293853463337001L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value="create_time")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 上次更新时间
     */
    @TableField(value="update_time")
    @Column(name = "update_time")
    private Date updateTime;
}
