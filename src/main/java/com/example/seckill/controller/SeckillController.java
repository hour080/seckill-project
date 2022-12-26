package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillOrderService;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/25 16:32
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    /**
     * 秒杀功能实现
     * @param model
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/25 16:37
     * @return java.lang.String
     */
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId){
        if(user == null){
            return "redirect:/login/toLogin";
        }
        model.addAttribute("user", user);

        //这里需要查询数据库获得真正的库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //如果库存不足（存在多线程问题）
        if(goodsVo.getSeckillStock() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMESSAGE());
            return "seckillFail";
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMESSAGE());
            return "seckillFail";
        }
        Order order = orderService.createOrderAndSeckillOrder(user, goodsVo);
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";
    }

}
