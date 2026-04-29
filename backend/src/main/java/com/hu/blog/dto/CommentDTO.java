package com.hu.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论DTO（含树形子评论）
 */
@Data
public class CommentDTO {
    private Long id;
    private String nickname;
    private String avatar;
    private String content;
    private Long parentId;
    private LocalDateTime createTime;
    /** 子评论列表 */
    private List<CommentDTO> children;
}
