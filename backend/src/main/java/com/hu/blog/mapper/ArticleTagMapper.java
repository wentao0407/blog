package com.hu.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    @Delete("DELETE FROM t_article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);
}
