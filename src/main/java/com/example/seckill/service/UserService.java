package com.example.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hourui
 * @since 2022-12-18
 */
public interface UserService extends IService<User> {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String cookie, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     * @author hourui
     * @date 2022/12/26 16:55
     * @return com.example.seckill.vo.RespBean
     */
    RespBean updatePassword(String cookie, String password, HttpServletRequest request, HttpServletResponse response);
}
