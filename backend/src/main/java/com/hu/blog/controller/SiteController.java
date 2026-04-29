package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 站点统计接口
 */
@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;

    @PostMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(siteService.stats());
    }
}
