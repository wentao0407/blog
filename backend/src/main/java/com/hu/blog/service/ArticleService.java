package com.hu.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.blog.dto.ArticleDTO;
import com.hu.blog.dto.ArticleDetailDTO;
import com.hu.blog.vo.ArticleListVO;
import com.hu.blog.vo.ArticleSaveVO;

import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 */
public interface ArticleService {

    /**
     * 分页查询文章列表
     *
     * @param vo 列表查询参数
     * @return 文章DTO分页结果
     */
    Page<ArticleDTO> list(ArticleListVO vo);

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情DTO
     */
    ArticleDetailDTO detail(Long id);

    /**
     * 获取置顶文章列表
     *
     * @return 置顶文章DTO列表
     */
    List<ArticleDTO> topArticles();

    /**
     * 获取文章归档列表（按月分组）
     *
     * @return 归档列表
     */
    List<Map<String, Object>> archive();

    /**
     * 保存文章（新增或更新）
     *
     * @param vo 文章保存参数
     */
    void save(ArticleSaveVO vo);

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    void delete(Long id);

    /**
     * 切换文章置顶状态
     *
     * @param id 文章ID
     */
    void toggleTop(Long id);
}
