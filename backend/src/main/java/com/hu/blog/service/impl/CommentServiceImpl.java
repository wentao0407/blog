package com.hu.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.blog.dto.CommentDTO;
import com.hu.blog.entity.Comment;
import com.hu.blog.entity.User;
import com.hu.blog.mapper.CommentMapper;
import com.hu.blog.mapper.UserMapper;
import com.hu.blog.service.CommentService;
import com.hu.blog.vo.CommentAddVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public List<CommentDTO> listByArticle(Long articleId) {
        // 查询该文章下所有评论
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .orderByDesc(Comment::getCreateTime));

        // 转换为DTO并构建树形结构
        List<CommentDTO> all = comments.stream().map(this::toDTO).collect(Collectors.toList());
        Map<Long, CommentDTO> map = all.stream().collect(Collectors.toMap(CommentDTO::getId, c -> c));
        List<CommentDTO> roots = new ArrayList<>();
        for (CommentDTO dto : all) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                // 顶级评论
                roots.add(dto);
            } else {
                // 子评论挂到父评论下
                CommentDTO parent = map.get(dto.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
                    parent.getChildren().add(dto);
                }
            }
        }
        return roots;
    }

    @Override
    public void add(CommentAddVO vo, Long userId) {
        Comment comment = new Comment();
        comment.setArticleId(vo.getArticleId());
        comment.setUserId(userId);
        comment.setNickname(vo.getNickname());
        comment.setContent(vo.getContent());
        // 父评论ID默认为0（顶级评论）
        comment.setParentId(vo.getParentId() != null ? vo.getParentId() : 0L);
        commentMapper.insert(comment);
    }

    @Override
    public void delete(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public List<CommentDTO> adminList(Long articleId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (articleId != null) wrapper.eq(Comment::getArticleId, articleId);
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> page = commentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 评论实体转DTO，附带头像信息
     */
    private CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        BeanUtil.copyProperties(comment, dto);
        // 查询用户头像
        if (comment.getUserId() != null) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) dto.setAvatar(user.getAvatar());
        }
        return dto;
    }
}
