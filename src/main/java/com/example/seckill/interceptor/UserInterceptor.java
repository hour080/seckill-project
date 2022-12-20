package com.example.seckill.interceptor;

import com.example.seckill.pojo.User;
import com.example.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/20 16:40
 */
public class UserInterceptor implements HandlerInterceptor {
    private UserService userService;

    public UserInterceptor(UserService userService) {
        this.userService = userService;
    }

    //在请求到达对应的controller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String ticket = null;
        if(cookies == null){
            response.sendRedirect("/login/toLogin"); //请求重定向
            return false;
        }
        for (Cookie cookie : cookies){
            if("userTicket".equals(cookie.getName())){
                ticket = cookie.getValue();
            }
        }
        if(!StringUtils.hasText(ticket)){
            //  请求转发
//            request.getRequestDispatcher("/login/toLogin").forward(request, response);
            response.sendRedirect("/login/toLogin"); //请求重定向
            return false;
        };
        User user = userService.getUserByCookie(ticket, request, response);//刷新cookie有效期
        if(user == null) {
            response.sendRedirect("/login/toLogin"); //请求重定向
            return false;
        }
        request.setAttribute("user", user); //request中可以直接存对象，只要到了后台都可以直接存对象
        return true;
    }
}
