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

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final StringRedisTemplate redisTemplate;

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

    @Override
    public List<ArticleDTO> topArticles() {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .eq(Article::getIsTop, 1)
                        .orderByDesc(Article::getCreateTime));
        return articles.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> archive() {
        return articleMapper.selectArchiveList();
    }

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

    @Override
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void toggleTop(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException("文章不存在");
        article.setIsTop(article.getIsTop() == 1 ? 0 : 1);
        articleMapper.updateById(article);
    }

    private ArticleDTO toDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        BeanUtil.copyProperties(article, dto);
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) dto.setCategoryName(category.getName());
        }
        List<ArticleTag> ats = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId()));
        if (!ats.isEmpty()) {
            List<Long> tagIds = ats.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
            List<String> tagNames = tagMapper.selectBatchIds(tagIds).stream()
                    .map(Tag::getName).collect(Collectors.toList());
            dto.setTagNames(tagNames);
        }
        String viewStr = redisTemplate.opsForValue().get("article:view:" + article.getId());
        if (viewStr != null) {
            dto.setViewCount(article.getViewCount() + Integer.parseInt(viewStr));
        }
        return dto;
    }
}
