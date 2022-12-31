package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.service.OrderService;
import com.example.seckill.vo.OrderDetailVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/toDetail")
    @ResponseBody
    public RespBean toDetail(User user, @RequestParam("orderId") Long orderId, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/toLogin");
            return RespBean.error(RespBeanEnum.USER_ERROR);
        }
        OrderDetailVo orderDetailVo = orderService.toDetail(orderId);
        return RespBean.success(orderDetailVo);
    }

}
