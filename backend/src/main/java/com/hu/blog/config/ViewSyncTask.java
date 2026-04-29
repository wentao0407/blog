package com.hu.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.blog.entity.Article;
import com.hu.blog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ViewSyncTask {

    private final StringRedisTemplate redisTemplate;
    private final ArticleMapper articleMapper;

    @Scheduled(fixedRate = 300000) // 每5分钟
    public void syncViewCount() {
        Set<String> keys = redisTemplate.keys("article:view:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            try {
                Long articleId = Long.parseLong(key.replace("article:view:", ""));
                String val = redisTemplate.opsForValue().get(key);
                if (val == null) continue;
                int increment = Integer.parseInt(val);
                if (increment > 0) {
                    articleMapper.update(null, new LambdaQueryWrapper<Article>()
                            .eq(Article::getId, articleId)
                            .setSql("view_count = view_count + " + increment));
                    redisTemplate.delete(key);
                }
            } catch (Exception e) {
                log.error("同步访问量失败: key={}", key, e);
            }
        }
        log.info("访问量同步完成");
    }
}
