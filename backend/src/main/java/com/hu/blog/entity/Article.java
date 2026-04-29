package com.hu.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_article")
public class Article extends BaseEntity {
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private Integer isTop;
    private Integer status;
    private Integer viewCount;
}
