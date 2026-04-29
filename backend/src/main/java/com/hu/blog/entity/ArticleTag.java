package com.hu.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文章标签关联实体
 */
@Data
@TableName("t_article_tag")
public class ArticleTag {
    private Long articleId;
    private Long tagId;
}
