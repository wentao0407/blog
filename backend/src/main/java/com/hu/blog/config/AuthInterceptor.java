package com.hu.blog.config;

import com.hu.blog.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器，校验Token并鉴权管理后台接口
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    public AuthInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录");
        }
        String userId = redisTemplate.opsForValue().get("user:token:" + token);
        if (userId == null) {
            throw new BusinessException(401, "登录已过期");
        }
        request.setAttribute("userId", Long.parseLong(userId));

        // 管理后台接口校验用户角色，防止普通用户越权访问
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/admin/")) {
            String roleStr = redisTemplate.opsForValue().get("user:role:" + token);
            if (roleStr == null || !roleStr.equals("1")) {
                throw new BusinessException(403, "无权限");
            }
        }

        return true;
    }
}
