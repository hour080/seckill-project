package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.vo.RespBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hourui
 * @since 2022-12-18
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 展示用户信息
     * @param user
     * @author hourui
     * @date 2022/12/25 19:59
     * @return com.example.seckill.vo.RespBean
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
