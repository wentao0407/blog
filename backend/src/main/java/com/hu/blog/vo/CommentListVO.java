package com.hu.blog.vo;

import lombok.Data;

/**
 * 评论列表查询VO
 */
@Data
public class CommentListVO {
    /** 文章ID（可选，按文章筛选） */
    private Long articleId;
    /** 页码 */
    private Integer pageNum = 1;
    /** 每页数量 */
    private Integer pageSize = 10;
}
