package com.hu.blog.controller.admin;

import com.hu.blog.common.Result;
import com.hu.blog.service.ArticleService;
import com.hu.blog.vo.ArticleSaveVO;
import com.hu.blog.vo.IdVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-文章管理接口
 */
@RestController
@RequestMapping("/api/admin/article")
@RequiredArgsConstructor
public class AdminArticleController {
    private final ArticleService articleService;

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody ArticleSaveVO vo) {
        articleService.save(vo);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@Valid @RequestBody IdVO vo) {
        articleService.delete(vo.getId());
        return Result.success();
    }

    @PostMapping("/top")
    public Result<Void> toggleTop(@Valid @RequestBody IdVO vo) {
        articleService.toggleTop(vo.getId());
        return Result.success();
    }
}
