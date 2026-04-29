package com.hu.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.blog.dto.ArticleDTO;
import com.hu.blog.dto.ArticleDetailDTO;
import com.hu.blog.vo.ArticleListVO;
import com.hu.blog.vo.ArticleSaveVO;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    Page<ArticleDTO> list(ArticleListVO vo);
    ArticleDetailDTO detail(Long id);
    List<ArticleDTO> topArticles();
    List<Map<String, Object>> archive();
    void save(ArticleSaveVO vo);
    void delete(Long id);
    void toggleTop(Long id);
}
