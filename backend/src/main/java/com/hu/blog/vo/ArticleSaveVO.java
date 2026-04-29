package com.hu.blog.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ArticleSaveVO {
    private Long id;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String summary;
    @NotBlank(message = "内容不能为空")
    private String content;
    private String coverImage;
    private Long categoryId;
    private List<Long> tagIds;
    private Integer isTop;
    private Integer status;
}
