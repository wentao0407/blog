package com.hu.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
public class Category extends BaseEntity {
    private String name;
    private Integer sort;
}
