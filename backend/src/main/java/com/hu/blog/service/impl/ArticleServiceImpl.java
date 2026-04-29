package com.hu.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.blog.dto.ArticleDTO;
import com.hu.blog.dto.ArticleDetailDTO;
import com.hu.blog.entity.Article;
import com.hu.blog.entity.ArticleTag;
import com.hu.blog.entity.Category;
import com.hu.blog.entity.Tag;
import com.hu.blog.exception.BusinessException;
import com.hu.blog.mapper.*;
import com.hu.blog.service.ArticleService;
import com.hu.blog.vo.ArticleListVO;
import com.hu.blog.vo.ArticleSaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * 分页查询已发布文章列表，支持关键词、分类、标签筛选
     */
    @Override
    public Page<ArticleDTO> list(ArticleListVO vo) {
        Page<Article> page = new Page<>(vo.getPageNum(), vo.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreateTime);

        if (vo.getKeyword() != null && !vo.getKeyword().isEmpty()) {
            wrapper.like(Article::getTitle, vo.getKeyword());
        }
        if (vo.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, vo.getCategoryId());
        }
        if (vo.getTagId() != null) {
            // 根据标签ID查找关联的文章ID列表
            List<ArticleTag> articleTags = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, vo.getTagId()));
            List<Long> articleIds = articleTags.stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
            if (articleIds.isEmpty()) {
                // 该标签下没有文章，返回空页
                Page<ArticleDTO> emptyPage = new Page<>(vo.getPageNum(), vo.getPageSize(), 0);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            wrapper.in(Article::getId, articleIds);
        }

        Page<Article> articlePage = articleMapper.selectPage(page, wrapper);
        List<Article> articles = articlePage.getRecords();

        if (articles.isEmpty()) {
            Page<ArticleDTO> dtoPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), 0);
            dtoPage.setRecords(Collections.emptyList());
            return dtoPage;
        }

        // 批量加载分类名称
        Set<Long> categoryIds = articles.stream().map(Article::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMapper.selectBatchIds(categoryIds).forEach(c -> categoryMap.put(c.getId(), c.getName()));
        }

        // 批量加载文章-标签关联
        List<Long> allArticleIds = articles.stream().map(Article::getId).collect(Collectors.toList());
        List<ArticleTag> allTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, allArticleIds));
        Map<Long, List<Long>> articleTagMap = allTags.stream().collect(
                Collectors.groupingBy(ArticleTag::getArticleId, Collectors.mapping(ArticleTag::getTagId, Collectors.toList())));

        // 批量加载标签名称
        Set<Long> allTagIds = allTags.stream().map(ArticleTag::getTagId).collect(Collectors.toSet());
        Map<Long, String> tagNameMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            tagMapper.selectBatchIds(allTagIds).forEach(t -> tagNameMap.put(t.getId(), t.getName()));
        }

        // 批量加载Redis浏览量
        List<String> viewKeys = allArticleIds.stream().map(id -> "article:view:" + id).collect(Collectors.toList());
        List<String> viewValues = redisTemplate.opsForValue().multiGet(viewKeys);
        Map<Long, Integer> redisViewMap = new HashMap<>();
        for (int i = 0; i < allArticleIds.size(); i++) {
            String val = viewValues != null ? viewValues.get(i) : null;
            if (val != null) {
                redisViewMap.put(allArticleIds.get(i), Integer.parseInt(val));
            }
        }

        // 组装DTO
        List<ArticleDTO> dtos = articles.stream().map(article -> {
            ArticleDTO dto = new ArticleDTO();
            BeanUtil.copyProperties(article, dto);
            dto.setCategoryName(categoryMap.get(article.getCategoryId()));
            List<Long> tagIds = articleTagMap.get(article.getId());
            if (tagIds != null && !tagIds.isEmpty()) {
                dto.setTagNames(tagIds.stream().map(tagNameMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            Integer redisViews = redisViewMap.get(article.getId());
            if (redisViews != null) {
                dto.setViewCount(article.getViewCount() + redisViews);
            }
            return dto;
        }).collect(Collectors.toList());

        Page<ArticleDTO> dtoPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        dtoPage.setRecords(dtos);
        return dtoPage;
    }

    /**
     * 获取文章详情，同时通过Redis累加浏览量
     */
    @Override
    public ArticleDetailDTO detail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != 1) {
            throw new BusinessException("文章不存在");
        }
        redisTemplate.opsForValue().increment("article:view:" + id);

        ArticleDetailDTO dto = new ArticleDetailDTO();
        BeanUtil.copyProperties(toDTO(article), dto);
        dto.setContent(article.getContent());
        // 回显文章关联的标签ID列表
        List<ArticleTag> articleTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId()));
        dto.setTagIds(articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList()));
        return dto;
    }

    /**
     * 获取所有置顶文章
     */
    @Override
    public List<ArticleDTO> topArticles() {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .eq(Article::getIsTop, 1)
                        .orderByDesc(Article::getCreateTime));
        if (articles.isEmpty()) return Collections.emptyList();
        return batchToDTO(articles);
    }

    /**
     * 获取文章归档（按月份分组，含文章列表）
     */
    @Override
    public List<Map<String, Object>> archive() {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getCreateTime));
        // 按月份分组
        Map<String, List<Article>> grouped = articles.stream().collect(
                Collectors.groupingBy(a -> a.getCreateTime().substring(0, 7), LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Article>> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("count", entry.getValue().size());
            item.put("articles", entry.getValue().stream().map(a -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", a.getId());
                m.put("title", a.getTitle());
                m.put("createTime", a.getCreateTime());
                return m;
            }).collect(Collectors.toList()));
            result.add(item);
        }
        return result;
    }

    /**
     * 保存文章（新增或更新），同时维护文章标签关联
     */
    @Override
    @Transactional
    public void save(ArticleSaveVO vo) {
        Article article = new Article();
        BeanUtil.copyProperties(vo, article);
        if (vo.getId() != null) {
            articleMapper.updateById(article);
            articleTagMapper.deleteByArticleId(vo.getId());
        } else {
            article.setViewCount(0);
            articleMapper.insert(article);
        }
        if (vo.getTagIds() != null) {
            for (Long tagId : vo.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
    }

    /**
     * 逻辑删除文章
     */
    @Override
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }

    /**
     * 切换文章置顶状态
     */
    @Override
    public void toggleTop(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException("文章不存在");
        article.setIsTop(article.getIsTop() == 1 ? 0 : 1);
        articleMapper.updateById(article);
    }

    /**
     * 批量将文章实体转为DTO，避免N+1查询
     */
    private List<ArticleDTO> batchToDTO(List<Article> articles) {
        List<Long> articleIds = articles.stream().map(Article::getId).collect(Collectors.toList());

        // 批量加载分类
        Set<Long> categoryIds = articles.stream().map(Article::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMapper.selectBatchIds(categoryIds).forEach(c -> categoryMap.put(c.getId(), c.getName()));
        }

        // 批量加载标签关联
        List<ArticleTag> allTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, articleIds));
        Map<Long, List<Long>> articleTagMap = allTags.stream().collect(
                Collectors.groupingBy(ArticleTag::getArticleId, Collectors.mapping(ArticleTag::getTagId, Collectors.toList())));

        // 批量加载标签名称
        Set<Long> allTagIds = allTags.stream().map(ArticleTag::getTagId).collect(Collectors.toSet());
        Map<Long, String> tagNameMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            tagMapper.selectBatchIds(allTagIds).forEach(t -> tagNameMap.put(t.getId(), t.getName()));
        }

        // 批量加载Redis浏览量
        List<String> viewKeys = articleIds.stream().map(id -> "article:view:" + id).collect(Collectors.toList());
        List<String> viewValues = redisTemplate.opsForValue().multiGet(viewKeys);
        Map<Long, Integer> redisViewMap = new HashMap<>();
        for (int i = 0; i < articleIds.size(); i++) {
            String val = viewValues != null ? viewValues.get(i) : null;
            if (val != null) {
                redisViewMap.put(articleIds.get(i), Integer.parseInt(val));
            }
        }

        // 组装DTO
        return articles.stream().map(article -> {
            ArticleDTO dto = new ArticleDTO();
            BeanUtil.copyProperties(article, dto);
            dto.setCategoryName(categoryMap.get(article.getCategoryId()));
            List<Long> tagIds = articleTagMap.get(article.getId());
            if (tagIds != null && !tagIds.isEmpty()) {
                dto.setTagNames(tagIds.stream().map(tagNameMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            Integer redisViews = redisViewMap.get(article.getId());
            if (redisViews != null) {
                dto.setViewCount(article.getViewCount() + redisViews);
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
