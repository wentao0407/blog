package com.hu.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

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
