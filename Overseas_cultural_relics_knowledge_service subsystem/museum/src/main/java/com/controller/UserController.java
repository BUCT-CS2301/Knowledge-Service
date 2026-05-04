package com.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mapper.UserMapper;
import com.service.IUserService;
import com.service.exception.PasswordNotMatchException;
import com.service.exception.UserLimitedLoginException;
import com.service.exception.UserNotFoundException;
import com.service.exception.userRepetitionException;
import com.util.JsonResult;
import com.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;
//    @RequestMapping("login")
////    @ResponseBody//表示此方法的响应结果以json格式进行数据的相应给前端
//    public JsonResult<User> login(@RequestParam("id") int user_id, @RequestParam("password") String user_password){//用户登录
//        JsonResult<User> result = new JsonResult<User>();
////            User data= iUserLoginService.log(username,userpassword);
//        try{
//            User u = userService.log(user_id,user_password);
//            result.setData(u);
//            result.setState(200);
//            result.setMessage("登录成功！欢迎你"+user_id);
//        }catch (UserNotFoundException e) {
//            result.setState(4000);
//            result.setMessage("用户不存在");
//        } catch (PasswordNotMatchException e) {
//            result.setState(6000);
//            result.setMessage("密码错误");
//        }
//        return result;
//    }
    @RequestMapping("/login")//用户登录
    JsonResult<User> login(@RequestBody Map map){
        JsonResult<User> result = new JsonResult<User>();
        try{
            String a = (String)map.get("username") ;
            String password = (String)map.get("password");
            int user_id = Integer.parseInt(a);
            User u = userService.log(user_id,password);
            String secret = "abcdef";
            String token = JWT.create().
                    withClaim("id",u.getUser_id()).//用户id
                    withClaim("username",u.getUser_name()).//用户名
                    sign(Algorithm.HMAC256(secret));
                    result.setState(200);
                    result.setMessage("登录成功！");
                    result.setToken(token);
                    result.setData(u);
                    return result;
        }catch(UserNotFoundException e){
            result.setMessage("用户不存在");
            result.setState(4000);
            return result;
        }catch(PasswordNotMatchException e){
            result.setMessage("密码错误");
            result.setState(6000);
            return result;
        }catch(UserLimitedLoginException e){
            result.setMessage("用户被限制登录");
            result.setState(9000);
            return result;
        }
    }

    @RequestMapping("/register")//用户注册
    public JsonResult<Integer> reg(@RequestBody Map map){//用户注册
        JsonResult<Integer> result = new JsonResult<Integer>();
        try {
            String user_password = (String)map.get("password");
            String user_name = (String)map.get("username");
            String s = (String)map.get("sex");
            int sex = Integer.parseInt(s);
            String user_tel = (String)map.get("tele");
            User user = userService.reg(user_password,user_name,sex,user_tel);
            result.setState(200);
            result.setData(user.getUser_id());
            result.setMessage("注册成功！欢迎你"+ user_name);
        }  catch(userRepetitionException e){
            result.setState(6000);
            result.setMessage("用户名重复！");
        }
        catch (Exception e) {
            result.setState(5000);
            result.setMessage("注册失败，请联系管理员");
        }
        return result;
    }
    @RequestMapping("/get_detail")//用户注册
    public JsonResult<User> findById(@RequestBody Map map){//用户注册
        JsonResult<User> result = new JsonResult<User>();
            String userid = (String)map.get("id");
            int user_id = Integer.parseInt(userid);
            User user = userMapper.findByUser_id(user_id);
            result.setState(200);
            result.setData(user);
            return result;
    }

}
