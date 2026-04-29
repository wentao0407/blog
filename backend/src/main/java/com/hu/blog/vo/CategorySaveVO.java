package com.hu.blog.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类保存 VO
 */
@Data
public class CategorySaveVO {
    private Long id;
    @NotBlank(message = "分类名不能为空")
    private String name;
    private Integer sort;
}
