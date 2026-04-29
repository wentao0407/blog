package com.hu.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章传输对象（前台列表出参）
 */
@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String summary;
    private String coverImage;
    private String categoryName;
    private List<String> tagNames;
    private Integer isTop;
    private Integer viewCount;
    private LocalDateTime createTime;
}
