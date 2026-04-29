package com.hu.blog.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增评论VO
 */
@Data
public class CommentAddVO {
    /** 文章ID */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    /** 用户ID（可为空，匿名评论） */
    private Long userId;
    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    private String nickname;
    /** 评论内容 */
    @NotBlank(message = "内容不能为空")
    private String content;
    /** 父评论ID */
    private Long parentId;
}
