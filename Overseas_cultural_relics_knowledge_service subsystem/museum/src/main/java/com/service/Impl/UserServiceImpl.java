package com.service.Impl;

import com.mapper.UserMapper;
import com.entity.User;
import com.service.IUserService;
import com.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;

//用户登录
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    private String salt = "museum";

//    @Autowired
//    private RedisTemplate redisTemplate;//缓存
    private String getMd5Password(String password, String salt) {
    /*
     * 加密规则：
     * 1、无视原始密码的强度
     * 2、使用UUID作为盐值，在原始密码的左右两侧拼接
     * 3、循环加密3次
     */
    for (int i = 0; i < 3; i++) {
        password = DigestUtils.md5DigestAsHex((salt + password +
                salt).getBytes()).toUpperCase();
    }
    return password;
}
    @Override
    public User log(int user_id, String user_password){
        User user = new User();
        User result;
        result = userMapper.findByUser_id(user_id);
        if(result == null) {
            throw new UserNotFoundException("用户不存在");//用户id输入错
        }
        String md5Password = getMd5Password(user_password, salt);
        User result2 = userMapper.findByUser_password(user_id,md5Password);
        if(result2 == null){
            throw new PasswordNotMatchException("用户密码错误");//用户pw输入错误
        }
        if(result2.getUser_login()==1)//登陆权限判断
        {
            user.setUser_id(user_id);
            user.setUser_password(md5Password);
            user.setUser_name(result2.getUser_name());
            user.setUser_sex(result2.getUser_sex());
            user.setUser_tel(result2.getUser_tel());
            user.setUser_comment(result2.getUser_comment());
            user.setUser_login(result2.getUser_login());
            user.toString();//日志
            return user;
        }
        else throw new UserLimitedLoginException("您由于不良言论过多，被限制访问");
    }

    @Override
    public User reg(String user_password,String user_name,int user_sex,String user_tel){
        User user = new User();
        String md5Password = getMd5Password(user_password, salt);
        user.setUser_password(md5Password);
        user.setUser_name(user_name);
        if(userMapper.findByUser_name(user.getUser_name())!=null)
            throw new userRepetitionException("用户名重复！");
        user.setUser_sex(user_sex);
        user.setUser_tel(user_tel);
        int row = userMapper.insert_user(user);
        if(row==1)
        {
            System.out.println("注册成功，影响行数为："+row);
            return userMapper.findByUser_name(user.getUser_name());
        }
        else throw new ServiceException("添加用户数据出现未知错误，请联系系统管理员");
    }

    @Override
    public int up(int user_id,String old_password,String user_password, String user_name, int user_sex, String user_tel) {
        String md5Password = getMd5Password(old_password, salt);
        User user = userMapper.findByUser_password(user_id,md5Password);
        User res = userMapper.findByUser_name(user_name);
//        if(res.getUser_id()!=user_id){
//            throw new UsernameCompetedException("用户名重复");
//        }
        if(user==null)
            throw new PasswordNotMatchException("原密码输入错误");
        else{
                int row = 0;
            String md5Password1 = getMd5Password(old_password, salt);
                user.setUser_password(md5Password1);
                user.setUser_name(user_name);
                user.setUser_sex(user_sex);
                user.setUser_tel(user_tel);
                row = userMapper.update(user);
                if(row!=0)
                {
                    System.out.println("Update success!");
                }
                else {
                    throw new ServiceException("Update fail");
                }
                return row;
        }
    }
    public int del(int user_id){
        User user = userMapper.findByUser_id(user_id);
        int row = 0;
        row = userMapper.delete(user);
        if(row!=0){
            return row;
        }
        else{
            throw new ServiceException("fail to delete the user!");
        }
    }

}
