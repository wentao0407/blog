package com.hu.blog.controller.admin;

import com.hu.blog.common.Result;
import com.hu.blog.service.ArticleService;
import com.hu.blog.vo.ArticleSaveVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Result<Void> delete(@RequestBody Map<String, Long> params) {
        articleService.delete(params.get("id"));
        return Result.success();
    }

    @PostMapping("/top")
    public Result<Void> toggleTop(@RequestBody Map<String, Long> params) {
        articleService.toggleTop(params.get("id"));
        return Result.success();
    }
}
