package com.hu.blog.dto;

import lombok.Data;

/**
 * 分类DTO（前台出参）
 */
@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private Integer sort;
}
