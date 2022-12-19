package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.pojo.User;
import com.example.seckill.service.UserService;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.ValidatorUtil;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    /**
     * 登陆功能实现
     * @param loginVo
     * @author hourui
     * @date 2022/12/18 21:21
     * @return com.example.seckill.vo.RespBean
     */
    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //很可能会绕过前端校验，因此后端必须再次检查
        if(!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //判断手机号码格式是否正确
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        //数据库查询是否存在
        User user = this.getById(mobile);
        if(user == null){
            return RespBean.error(RespBeanEnum.USER_ERROR);
        }
        //判断密码是否正确
        if(!MD5Util.serverToDBPass(password, user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        return RespBean.success();
    }
}
