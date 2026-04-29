package com.hu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.blog.entity.Category;
import com.hu.blog.mapper.CategoryMapper;
import com.hu.blog.service.CategoryService;
import com.hu.blog.vo.CategorySaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> list() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    @Override
    public void save(CategorySaveVO vo) {
        Category category = new Category();
        category.setId(vo.getId());
        category.setName(vo.getName());
        category.setSort(vo.getSort() != null ? vo.getSort() : 0);
        if (vo.getId() != null) {
            categoryMapper.updateById(category);
        } else {
            categoryMapper.insert(category);
        }
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
