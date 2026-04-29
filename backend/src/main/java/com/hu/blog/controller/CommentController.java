package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.dto.CommentDTO;
import com.hu.blog.service.CommentService;
import com.hu.blog.vo.CommentAddVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论接口（前台）
 */
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * 获取文章的树形评论列表
     */
    @PostMapping("/list")
    public Result<List<CommentDTO>> list(@RequestBody Map<String, Long> params) {
        return Result.success(commentService.listByArticle(params.get("articleId")));
    }

    /**
     * 新增评论（支持登录/匿名）
     */
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CommentAddVO vo,
                            @RequestAttribute(required = false) Long userId) {
        commentService.add(vo, userId);
        return Result.success();
    }
}
