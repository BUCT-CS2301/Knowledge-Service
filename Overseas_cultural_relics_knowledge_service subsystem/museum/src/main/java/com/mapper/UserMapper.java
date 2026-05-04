package com.mapper;

import com.entity.User;

public interface UserMapper {
    User findByUser_id(int user_id);
    User findByUser_password(int user_id, String user_password);
    int insert_user(User user);
    int update(User user);//修改用户个人资料
    int delete(User user);//注销用户
    User findByUser_name(String user_name);
}
