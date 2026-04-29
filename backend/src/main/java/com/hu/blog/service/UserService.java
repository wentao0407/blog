package com.hu.blog.service;

import com.hu.blog.vo.LoginVO;
import com.hu.blog.vo.RegisterVO;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginVO vo);
    void register(RegisterVO vo);
    Map<String, Object> getUserInfo(Long userId);
}
