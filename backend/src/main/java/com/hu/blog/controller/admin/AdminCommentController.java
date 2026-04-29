package com.hu.blog.controller.admin;

import com.hu.blog.common.Result;
import com.hu.blog.service.CommentService;
import com.hu.blog.vo.IdVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评论管理接口（后台）
 */
@RestController
@RequestMapping("/api/admin/comment")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    /**
     * 分页查询评论列表
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody Map<String, Object> params) {
        Long articleId = params.get("articleId") != null ? Long.valueOf(params.get("articleId").toString()) : null;
        Integer pageNum = params.get("pageNum") != null ? Integer.valueOf(params.get("pageNum").toString()) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.valueOf(params.get("pageSize").toString()) : 10;
        return Result.success(commentService.adminList(articleId, pageNum, pageSize));
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete")
    public Result<Void> delete(@Valid @RequestBody IdVO vo) {
        commentService.delete(vo.getId());
        return Result.success();
    }
}
