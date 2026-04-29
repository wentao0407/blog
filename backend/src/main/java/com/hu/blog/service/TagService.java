package com.hu.blog.service;

import com.hu.blog.entity.Tag;
import com.hu.blog.vo.TagSaveVO;
import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 查询所有标签
     *
     * @return 标签列表
     */
    List<Tag> list();

    /**
     * 保存标签（新增或更新）
     *
     * @param vo 标签保存参数
     */
    void save(TagSaveVO vo);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void delete(Long id);
}
