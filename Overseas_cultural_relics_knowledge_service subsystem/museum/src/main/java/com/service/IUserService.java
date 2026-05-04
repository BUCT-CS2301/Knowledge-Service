package com.service;

import com.entity.User;

public interface IUserService {
    User log(int user_id, String user_password);
    User reg(String user_password,String user_name,int user_sex,String user_tel);
    int up(int user_id,String old_password,String user_password,String user_name,int user_sex,String user_tel);
    int del(int user_id);

}
