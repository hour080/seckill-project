package com.example.seckill.controller;

import com.example.seckill.pojo.User;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.UserService;
import com.example.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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

    @Autowired
    private GoodsService goodsService;
    /**
     * 登陆成功以后进行商品页面的跳转
     * @param user 用户信息,这里的用户信息会由参数解析器提供
     * @param model 将已经登陆的用户信息展示在商品页面
     * @author hourui
     * @date 2022/12/19 21:20
     * @return java.lang.String
     */
    @RequestMapping("/toList")
    public String toList(Model model, User user){
        if(user == null){
            return "redirect:/login/toLogin"; //重定向到login界面, 关键还是要看视图解析器ThymeleafViewResolver中的createView的处理逻辑。
        }
        log.info("跳转商品页");
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }

    /**
     * 跳转商品详情页
     * @param goodsId
     * @author hourui
     * @date 2022/12/24 16:03
     * @return java.lang.String
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable("goodsId") Long goodsId){
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int seckillStatus = 0; //秒杀状态
        int remainSeconds; //秒杀剩余倒计时
        //秒杀尚未开始
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if(nowDate.after(endDate)){
            seckillStatus = 2; //秒杀已经结束
            remainSeconds = -1;
        }else{
            seckillStatus = 1; //秒杀正在进行中
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("goods", goodsVo);
        return "goodsDetail";
    }
}
