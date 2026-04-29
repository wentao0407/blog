package com.hu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.blog.entity.Tag;
import com.hu.blog.mapper.TagMapper;
import com.hu.blog.service.TagService;
import com.hu.blog.vo.TagSaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    public List<Tag> list() {
        return tagMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public void save(TagSaveVO vo) {
        Tag tag = new Tag();
        tag.setId(vo.getId());
        tag.setName(vo.getName());
        if (vo.getId() != null) {
            tagMapper.updateById(tag);
        } else {
            tagMapper.insert(tag);
        }
    }

    @Override
    public void delete(Long id) {
        tagMapper.deleteById(id);
    }
}
