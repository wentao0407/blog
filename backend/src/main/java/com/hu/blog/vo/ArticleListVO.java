package com.hu.blog.vo;

import lombok.Data;

@Data
public class ArticleListVO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long categoryId;
    private Long tagId;
}
