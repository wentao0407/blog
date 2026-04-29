package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.dto.TagDTO;
import com.hu.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签接口（前台）
 */
@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping("/list")
    public Result<List<TagDTO>> list() {
        return Result.success(tagService.list().stream().map(tag -> {
            TagDTO dto = new TagDTO();
            dto.setId(tag.getId());
            dto.setName(tag.getName());
            return dto;
        }).collect(Collectors.toList()));
    }
}
