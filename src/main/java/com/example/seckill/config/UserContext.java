package com.example.seckill.config;

import com.example.seckill.pojo.User;

/**
 * TODO
 * ThreadLocal 用于存储AccessLimitInterceptor获得的用户信息
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/31 16:35
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
