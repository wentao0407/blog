package com.hu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hu.blog.entity.Article;
import com.hu.blog.entity.Comment;
import com.hu.blog.mapper.ArticleMapper;
import com.hu.blog.mapper.CommentMapper;
import com.hu.blog.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
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

        // Use SQL SUM for total views instead of loading all articles into memory
        Long dbViews = articleMapper.selectObjs(
                new QueryWrapper<Article>()
                        .eq("status", 1)
                        .select("COALESCE(SUM(view_count), 0)")
                        .last("LIMIT 1"))
                .stream().findFirst().map(v -> ((Number) v).longValue()).orElse(0L);

        // Sum Redis incremental views using SCAN
        Set<String> keys = new HashSet<>();
        redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Void>) connection -> {
            try (var cursor = connection.scan(
                    org.springframework.data.redis.core.ScanOptions.scanOptions().match("article:view:*").count(100).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                log.error("SCAN 扫描失败", e);
            }
            return null;
        });

        long redisViews = 0;
        if (!keys.isEmpty()) {
            List<String> vals = redisTemplate.opsForValue().multiGet(keys);
            if (vals != null) {
                for (String v : vals) {
                    if (v != null) redisViews += Long.parseLong(v);
                }
            }
        }

        stats.put("viewCount", dbViews + redisViews);
        return stats;
    }
}
