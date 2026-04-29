package com.hu.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hu.blog.entity.Article;
import com.hu.blog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 访问量同步定时任务，定期将Redis中的浏览量增量写回数据库
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ViewSyncTask {

    private final StringRedisTemplate redisTemplate;
    private final ArticleMapper articleMapper;

    @Scheduled(fixedRate = 300000) // 每5分钟
    public void syncViewCount() {
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

        if (keys.isEmpty()) return;

        // Lua脚本：原子读取并重置key，避免竞态条件丢失访问量
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(
                "local v = redis.call('GET', KEYS[1]) " +
                "if v then redis.call('SET', KEYS[1], '0') end " +
                "return v", Long.class);

        for (String key : keys) {
            try {
                Long articleId = Long.parseLong(key.replace("article:view:", ""));
                Long increment = redisTemplate.execute(script, Collections.singletonList(key));
                if (increment != null && increment > 0) {
                    articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                            .eq(Article::getId, articleId)
                            .setSql("view_count = view_count + " + increment));
                }
            } catch (Exception e) {
                log.error("同步访问量失败: key={}", key, e);
            }
        }
        log.info("访问量同步完成，处理 {} 个key", keys.size());
    }
}
