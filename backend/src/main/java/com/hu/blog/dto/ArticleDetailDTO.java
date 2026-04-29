package com.hu.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailDTO extends ArticleDTO {
    private String content;
    private List<Long> tagIds;
}
