package com.hu.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tag")
public class Tag extends BaseEntity {
    private String name;
}
