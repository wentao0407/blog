package com.hu.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailDTO extends ArticleDTO {
    private String content;
}
