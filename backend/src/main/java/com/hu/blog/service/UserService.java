package com.hu.blog.service;

import com.hu.blog.vo.LoginVO;
import com.hu.blog.vo.RegisterVO;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param vo 登录请求参数
     * @return 包含token和用户信息的Map
     */
    Map<String, Object> login(LoginVO vo);

    /**
     * 用户注册
     *
     * @param vo 注册请求参数
     */
    void register(RegisterVO vo);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息Map
     */
    Map<String, Object> getUserInfo(Long userId);
}
