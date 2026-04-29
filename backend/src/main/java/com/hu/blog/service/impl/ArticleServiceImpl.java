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

        Page<Article> articlePage = articleMapper.selectPage(page, wrapper);
        Page<ArticleDTO> dtoPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        dtoPage.setRecords(articlePage.getRecords().stream().map(this::toDTO).collect(Collectors.toList()));
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
