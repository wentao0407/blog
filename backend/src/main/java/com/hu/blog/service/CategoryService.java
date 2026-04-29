package com.hu.blog.service;

import com.hu.blog.entity.Category;
import com.hu.blog.vo.CategorySaveVO;
import java.util.List;

public interface CategoryService {
    List<Category> list();
    void save(CategorySaveVO vo);
    void delete(Long id);
}
