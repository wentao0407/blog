package com.hu.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.blog.common.Result;
import com.hu.blog.dto.ArticleDTO;
import com.hu.blog.dto.ArticleDetailDTO;
import com.hu.blog.service.ArticleService;
import com.hu.blog.vo.ArticleListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/list")
    public Result<Page<ArticleDTO>> list(@RequestBody ArticleListVO vo) {
        return Result.success(articleService.list(vo));
    }

    @PostMapping("/detail")
    public Result<ArticleDetailDTO> detail(@RequestBody Map<String, Long> params) {
        return Result.success(articleService.detail(params.get("id")));
    }

    @PostMapping("/top")
    public Result<List<ArticleDTO>> top() {
        return Result.success(articleService.topArticles());
    }

    @PostMapping("/archive")
    public Result<List<Map<String, Object>>> archive() {
        return Result.success(articleService.archive());
    }
}
