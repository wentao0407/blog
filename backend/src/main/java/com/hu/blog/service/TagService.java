package com.hu.blog.service;

import com.hu.blog.entity.Tag;
import com.hu.blog.vo.TagSaveVO;
import java.util.List;

public interface TagService {
    List<Tag> list();
    void save(TagSaveVO vo);
    void delete(Long id);
}
