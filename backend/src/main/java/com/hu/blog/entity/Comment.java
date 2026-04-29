package com.hu.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_comment")
public class Comment extends BaseEntity {
    /** 文章ID */
    private Long articleId;
    /** 用户ID（可为空，匿名评论） */
    private Long userId;
    /** 昵称 */
    private String nickname;
    /** 评论内容 */
    private String content;
    /** 父评论ID（0表示顶级评论） */
    private Long parentId;
}
