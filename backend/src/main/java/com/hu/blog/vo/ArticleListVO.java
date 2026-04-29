package com.hu.blog.vo;

import lombok.Data;

/**
 * 文章列表查询 VO
 */
@Data
public class ArticleListVO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long categoryId;
    private Long tagId;
}
