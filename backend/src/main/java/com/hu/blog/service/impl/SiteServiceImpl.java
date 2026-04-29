package com.hu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.blog.entity.Article;
import com.hu.blog.entity.Comment;
import com.hu.blog.mapper.ArticleMapper;
import com.hu.blog.mapper.CommentMapper;
import com.hu.blog.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("articleCount", articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)));
        stats.put("commentCount", commentMapper.selectCount(new LambdaQueryWrapper<>()));
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));
        long totalViews = 0;
        for (Article a : articles) {
            totalViews += a.getViewCount();
            String redisView = redisTemplate.opsForValue().get("article:view:" + a.getId());
            if (redisView != null) {
                totalViews += Long.parseLong(redisView);
            }
        }
        stats.put("viewCount", totalViews);
        return stats;
    }
}
