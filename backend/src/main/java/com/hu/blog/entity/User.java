package com.hu.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private Integer role;
}
