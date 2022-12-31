package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.pojo.User;
import com.example.seckill.service.UserService;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.utils.ValidatorUtil;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hourui
 * @since 2022-12-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 登陆功能实现
     * @param loginVo
     * @author hourui
     * @date 2022/12/18 21:21
     * @return com.example.seckill.vo.RespBean
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //很可能会绕过前端校验，因此后端必须再次进行参数校验
//        if(!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        //判断手机号码格式是否正确
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //数据库查询是否存在
        User user = this.getById(mobile);
        if(user == null){
            throw new GlobalException(RespBeanEnum.USER_ERROR);
        }
        //判断密码是否正确
        if(!MD5Util.serverToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.PASSWORD_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        //生成的cookie值和用户存到session中去
//        HttpSession session = request.getSession();
//        session.setAttribute(ticket, user);
        //将用户信息保存到redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        //Cookie cookie = new Cookie(cookieName, cookieValue); response.addCookie(cookie)
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String cookie, HttpServletRequest request, HttpServletResponse response) {
        if(!StringUtils.hasText(cookie)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + cookie);
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", cookie);
        }
        return user;
    }

    /**
     * 更新密码
     * 由于缓存和数据库中都保存了用户数据，因此如果修改用户的密码
     * 如何保证缓存和数据库的数据一致性
     * 更新数据库的同时删除缓存，并且保证更新数据库和删除缓存的原子性
     * @param cookie
     * @param password
     * @author hourui
     * @date 2022/12/26 17:00
     * @return com.example.seckill.vo.RespBean
     */
    @Override
    public RespBean updatePassword(String cookie, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(cookie, request, response);
        if(user == null){
            throw new GlobalException(RespBeanEnum.USER_ERROR);
        }
        user.setPassword(MD5Util.inputToDBPass(password, user.getSalt()));
        boolean result = save(user);//更新数据库中的用户信息
        if(result){
            redisTemplate.delete("user:" + cookie); //删除缓存
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAILED);
    }
}
