package com.hu.blog.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.blog.entity.User;
import com.hu.blog.exception.BusinessException;
import com.hu.blog.mapper.UserMapper;
import com.hu.blog.service.UserService;
import com.hu.blog.vo.LoginVO;
import com.hu.blog.vo.RegisterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * 用户登录，验证用户名密码并生成Token存入Redis
     */
    @Override
    public Map<String, Object> login(LoginVO vo) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, vo.getUsername()));
        if (user == null || !BCrypt.checkpw(vo.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("user:token:" + token, user.getId().toString(), 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("user:role:" + token, user.getRole().toString(), 7, TimeUnit.DAYS);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", user.getId());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole());
        return result;
    }

    /**
     * 用户注册，校验用户名唯一性后创建用户
     */
    @Override
    public void register(RegisterVO vo) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, vo.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(vo.getUsername());
        user.setPassword(BCrypt.hashpw(vo.getPassword()));
        user.setNickname(vo.getNickname());
        user.setRole(0);
        userMapper.insert(user);
    }

    /**
     * 根据用户ID获取用户信息
     */
    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        return result;
    }
}
