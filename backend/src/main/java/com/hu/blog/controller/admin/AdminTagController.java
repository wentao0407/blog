package com.hu.blog.controller.admin;

import com.hu.blog.common.Result;
import com.hu.blog.service.TagService;
import com.hu.blog.vo.TagSaveVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tag")
@RequiredArgsConstructor
public class AdminTagController {
    private final TagService tagService;

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody TagSaveVO vo) {
        tagService.save(vo);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Long id) {
        tagService.delete(id);
        return Result.success();
    }
}
