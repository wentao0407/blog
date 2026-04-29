package com.hu.blog.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增评论VO
 */
@Data
public class CommentAddVO {
    /** 文章ID */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不能超过50字")
    private String nickname;
    /** 评论内容 */
    @NotBlank(message = "内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000字")
    private String content;
    /** 父评论ID */
    private Long parentId;
}
