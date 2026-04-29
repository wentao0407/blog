package com.hu.blog.service;

import com.hu.blog.dto.CommentDTO;
import com.hu.blog.vo.CommentAddVO;
import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface CommentService {
    /** 获取文章的树形评论列表 */
    List<CommentDTO> listByArticle(Long articleId);
    /** 新增评论 */
    void add(CommentAddVO vo, Long userId);
    /** 删除评论 */
    void delete(Long id);
    /** 管理端分页查询评论（返回 records + total） */
    Map<String, Object> adminList(Long articleId, Integer pageNum, Integer pageSize);
}
