package com.hu.blog.controller;

import com.hu.blog.common.Result;
import com.hu.blog.service.UserService;
import com.hu.blog.vo.LoginVO;
import com.hu.blog.vo.RegisterVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginVO vo) {
        return Result.success(userService.login(vo));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterVO vo) {
        userService.register(vo);
        return Result.success();
    }

    @PostMapping("/info")
    public Result<Map<String, Object>> info(@RequestAttribute(required = false) Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }
}
