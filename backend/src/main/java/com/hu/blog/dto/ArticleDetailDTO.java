package com.hu.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文章详情传输对象（含正文内容和标签ID列表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailDTO extends ArticleDTO {
    private Long categoryId;
    private String content;
    private List<Long> tagIds;
}
