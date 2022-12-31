package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckill.config.AccessLimit;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillMessage;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillOrderService;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PipedReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/25 16:32
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript<Long> redisScript;

    @Autowired
    private RedissonClient redissonClient;

    //判断商品id为goodsId的商品是否还有库存
    private Map<Long, Boolean> emptyStockMap = new ConcurrentHashMap<>();
    /**
     * bean对象初始化时，执行该方法，把商品数量添加到redis中
     * @author hourui
     * @date 2022/12/28 17:24
     * @return void
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoOfList = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(goodsVoOfList)) return;
        Map<String, Object> map = new HashMap<>();
        goodsVoOfList.forEach(goodsVo -> {
            map.put("seckillGoods:" + goodsVo.getId(),  goodsVo.getSeckillStock());
            emptyStockMap.put(goodsVo.getId(), false);
        });
        redisTemplate.opsForValue().multiSet(map);
    }
    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/30 22:11
     * @return com.example.seckill.vo.RespBean
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @GetMapping("/getPath")
    @ResponseBody
    public RespBean getPath(User user, @RequestParam("goodsId") Long goodsId,
                            @RequestParam("result") String result,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/toLogin");
            return RespBean.error(RespBeanEnum.USER_ERROR);
        }
        //验证码校验通过才可以获得秒杀地址
        boolean check = orderService.checkCaptcha(user, goodsId, result);
        if(!check) return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }
    /**
     * 前端查询秒杀结果
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/29 18:04
     * @return com.example.seckill.vo.RespBean
     */
    @GetMapping(value = "/result/{goodsId}")
    @ResponseBody
    public RespBean getResult(User user, @PathVariable("goodsId") Long goodsId, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/toLogin");
            return RespBean.error(RespBeanEnum.USER_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @PostMapping(value = "/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable("path") String path, User user, Long goodsId, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/toLogin");
            return RespBean.error(RespBeanEnum.USER_ERROR);
        }
        //通过内存标记来减少对redis中库存数量的访问，如果是空库存，就不需要和redis交互来减少库存数量了。
        if(emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //检查路径是否正确
        boolean check = orderService.checkPath(user, goodsId, path);
        //查看路径是否匹配
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //锁的key是lock:order:userID ,value是线程id
        RLock lock = redissonClient.getLock("lock:order:" + user.getId());
        boolean isLock = lock.tryLock();
        if(!isLock){
            // 获取锁失败，直接返回失败或者重试
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        try {
            //解决一人多单的问题
            SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
            if(seckillOrder != null){
                return RespBean.error(RespBeanEnum.REPEAT_ERROR);
            }
            //解决库存超卖问题
            //redis中的递减库存，这个操作是一个原子操作，多个线程获得的是不一样的结果。
            //一般扣减库存并不是一个原子操作，stock--分为3步，将内存中的值赋值到寄存器中，寄存器减1，将寄存器中的值赋值回内存
            //Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
            Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
            if(stock == -1){ //库存不存在
                return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
            }
            else if(stock == 0){
                emptyStockMap.put(goodsId, true);
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
            SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
            ObjectMapper objectMapper = new ObjectMapper();
            //进行消息的序列化，将序列化后的消息发送到交换机，消息中带有seckill.message的routingKey
            mqSender.sendSecKillMessage(objectMapper.writeValueAsString(seckillMessage));
            return RespBean.success(0);
        } finally {
            while(true){
                if(redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId) == null){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    break;
                }
            }
            lock.unlock();
        }
    }
    @GetMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if(user == null || goodsId == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache"); //设置不需要缓存，防止重新刷出一样的验证码
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 算术类型, 生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 5, TimeUnit.MINUTES);
//        captcha.getArithmeticString();  // 获取运算的公式：3+2=?
//        captcha.text();  // 获取运算的结果：5
        try {
            captcha.out(response.getOutputStream());  // 输出验证码到前端界面
        } catch (IOException e) {
            log.error("验证码生成失败:{}", e.getMessage());
        }
    }
    /**
     * 秒杀功能实现
     * 秒杀静态化
     * @param model
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/25 16:37
     * @return java.lang.String
     */
    @PostMapping(value = "/doSeckillByStatic")
    @ResponseBody
    public RespBean doSeckillByStatic(Model model, User user, Long goodsId, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/toLogin");
            return RespBean.error(RespBeanEnum.USER_ERROR);
        }
        model.addAttribute("user", user);
        //这里需要查询数据库获得真正的库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        if(nowDate.before(startDate) || nowDate.after(endDate)){
            return RespBean.error(RespBeanEnum.TIME_ERROR);
        }
        //如果库存不足（存在多线程问题）
        if(goodsVo.getSeckillStock() < 1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购,这样就不去数据库中查询，而是去redis中查询
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
//                .eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        Order order = orderService.createOrderAndSeckillOrder(user, goodsVo);
        return RespBean.success(order);
    }
    /**
     * 秒杀功能实现
     * @param model
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/25 16:37
     * @return java.lang.String
     */
    @RequestMapping("/doSeckillByThymeleaf")
    public String doSeckillByThymeleaf(Model model, User user, Long goodsId){
        if(user == null){
            return "redirect:/login/toLogin";
        }
        model.addAttribute("user", user);
        //这里需要查询数据库获得真正的库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        if(nowDate.before(startDate) || nowDate.after(endDate)){
            model.addAttribute("errmsg", RespBeanEnum.TIME_ERROR.getMESSAGE());
            return "secKillFail";
        }
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
