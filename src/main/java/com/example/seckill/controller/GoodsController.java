package com.example.seckill.controller;

import com.example.seckill.pojo.User;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.UserService;
import com.example.seckill.vo.DetailVo;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    /**
     * 登陆成功以后进行商品页面的跳转
     * @param user 用户信息,这里的用户信息会由参数解析器提供
     * @param model 将已经登陆的用户信息展示在商品页面
     * @author hourui
     * @date 2022/12/19 21:20
     * @return java.lang.String
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8") //produces指定返回值类型和返回值编码
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/toLogin");//重定向到login界面,
            return "";
        }
        String html = (String) redisTemplate.opsForValue().get("goodsList");
        if(StringUtils.hasText(html)){
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
//        return "goodsList";
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //将后端数据和原有的html页面进行动态渲染，返回渲染后的html字符串
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if(StringUtils.hasText(html)){
            redisTemplate.opsForValue().set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页
     * 前后端分离
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/27 10:44
     * @return java.lang.String
     */

    @RequestMapping(value = "/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable("goodsId") Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int seckillStatus = 0; //秒杀状态
        int remainSeconds; //秒杀剩余倒计时
        int lastSeconds; //秒杀结束剩余倒计时
        //秒杀尚未开始
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime()) / 1000);
            lastSeconds = (int)((endDate.getTime() - startDate.getTime()) / 1000);
        }else if(nowDate.after(endDate)){
            seckillStatus = 2; //秒杀已经结束
            remainSeconds = -1;
            lastSeconds = -1;
        }else{
            lastSeconds = (int)((endDate.getTime() - nowDate.getTime()) / 1000);
            seckillStatus = 1; //秒杀正在进行中
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSeckillStatus(seckillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setLastSeconds(lastSeconds);
        return RespBean.success(detailVo);
    }


    /**
     * 跳转商品详情页（页面缓存）
     * @param goodsId
     * @author hourui
     * @date 2022/12/24 16:03
     * @return java.lang.String
     */
    @RequestMapping(value = "/toDetailByPageCache/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetailByPageCache(Model model, User user, @PathVariable("goodsId") Long goodsId, HttpServletRequest request, HttpServletResponse response){
        String html = (String) redisTemplate.opsForValue().get("goodsDetails:" + goodsId);
        if(StringUtils.hasText(html)){
            return html;
        }
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
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
        if(StringUtils.hasText(html)){
            redisTemplate.opsForValue().set("goodsDetails:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }
}
