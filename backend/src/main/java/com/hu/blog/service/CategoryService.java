package com.hu.blog.service;

import com.hu.blog.entity.Category;
import com.hu.blog.vo.CategorySaveVO;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    List<Category> list();

    /**
     * 保存分类（新增或更新）
     *
     * @param vo 分类保存参数
     */
    void save(CategorySaveVO vo);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void delete(Long id);
}
