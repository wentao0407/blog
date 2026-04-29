package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.entity.Tag;
import com.hu.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping("/list")
    public Result<List<Tag>> list() {
        return Result.success(tagService.list());
    }
}
