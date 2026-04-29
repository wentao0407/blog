package com.hu.blog.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通用ID入参VO
 */
@Data
public class IdVO {
    @NotNull(message = "ID不能为空")
    private Long id;
}
