package com.hu.blog.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签保存 VO
 */
@Data
public class TagSaveVO {
    private Long id;
    @NotBlank(message = "标签名不能为空")
    private String name;
}
