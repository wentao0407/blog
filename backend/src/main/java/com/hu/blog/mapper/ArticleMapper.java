package com.hu.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.blog.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS count " +
            "FROM t_article WHERE status = 1 AND deleted = 0 " +
            "GROUP BY month ORDER BY month DESC")
    List<Map<String, Object>> selectArchiveList();
}
