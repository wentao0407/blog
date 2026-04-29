package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.dto.CategoryDTO;
import com.hu.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类接口（前台）
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/list")
    public Result<List<CategoryDTO>> list() {
        return Result.success(categoryService.list().stream().map(cat -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            dto.setSort(cat.getSort());
            return dto;
        }).collect(Collectors.toList()));
    }
}
