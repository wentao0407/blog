package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.entity.Category;
import com.hu.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
}
