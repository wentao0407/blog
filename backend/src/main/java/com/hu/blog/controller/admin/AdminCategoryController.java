package com.hu.blog.controller.admin;

import com.hu.blog.common.Result;
import com.hu.blog.service.CategoryService;
import com.hu.blog.vo.CategorySaveVO;
import com.hu.blog.vo.IdVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-分类管理接口
 */
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody CategorySaveVO vo) {
        categoryService.save(vo);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@Valid @RequestBody IdVO vo) {
        categoryService.delete(vo.getId());
        return Result.success();
    }
}
