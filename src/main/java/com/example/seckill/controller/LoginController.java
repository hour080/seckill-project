package com.example.seckill.controller;

import com.example.seckill.service.UserService;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/18 17:27
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    UserService userService;

    /**
     * 跳转至登陆页面
     * @author hourui
     * @date 2022/12/18 17:29
     * @return java.lang.String
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
    /**
     * 登陆界面点击登陆后跳转到该函数
     * @param loginVo 用于接收登陆界面传递来的用户名和密码
     * @author hourui
     * @date 2022/12/18 21:12
     * @return com.example.seckill.vo.RespBean
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo){
        return userService.doLogin(loginVo);
    }
}
