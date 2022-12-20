package com.example.seckill.controller;

import com.example.seckill.pojo.User;
import com.example.seckill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/19 21:19
 */
@Controller
@Slf4j
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;
    /**
     * 登陆成功以后进行商品页面的跳转
     * @param user 用户信息
     * @param model 将已经登陆的用户信息展示在商品页面
     * @author hourui
     * @date 2022/12/19 21:20
     * @return java.lang.String
     */
    @RequestMapping("/toList")
    public String toList(Model model, User user){
        if(user == null){
            return "login";
        }
        log.info("跳转商品页");
        model.addAttribute("user", user);
        return "goodsList";
    }
}
