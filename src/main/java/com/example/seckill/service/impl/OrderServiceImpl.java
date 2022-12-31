package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillGoods;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillGoodsService;
import com.example.seckill.service.SeckillOrderService;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.OrderDetailVo;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建订单以及秒杀订单
     * @param user
     * @param goodsVo
     * @author hourui
     * @date 2022/12/25 17:07
     * @return com.example.seckill.pojo.Order
     */
    @Override
    @Transactional
    public Order createOrderAndSeckillOrder(User user, GoodsVo goodsVo) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        //如果容量小于1，等于0，则无法扣减库存
        if(seckillGoods.getSeckillStock() < 1){
            redisTemplate.opsForValue().set("isStockEmpty:" + goodsVo.getId(), "0");
            return null;
        }
        seckillGoods.setSeckillStock(seckillGoods.getSeckillStock() - 1);
        //使用乐观锁解决超卖问题， 由于存在唯一索引，因此其他线程会更新失败，返回false
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("seckill_stock = seckill_stock - 1")
                .eq("goods_id", goodsVo.getId())
                .gt("seckill_stock", 0));
        if(seckillGoodsResult == false) return null;
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1); //参加的是秒杀活动，因此每件商品只能购买一件
        order.setGoodsPrice(goodsVo.getSeckillPrice()); //商品的价格自然也就是秒杀价格
        order.setOrderChannel(1);
        order.setStatus(0); //新建状态
        order.setCreateDate(new Date());
        save(order); //自动回传生成的主键给对象
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), seckillOrder);
        return order;
    }

    /**
     * 生成订单详情
     * @param orderId
     * @author hourui
     * @date 2022/12/27 17:28
     * @return com.example.seckill.vo.OrderDetailVo
     */
    @Override
    public OrderDetailVo toDetail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = this.getById(orderId);
        Long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @author hourui
     * @date 2022/12/30 22:13
     * @return java.lang.String
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @author hourui
     * @date 2022/12/30 22:21
     * @return boolean
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user == null || goodsId == null || goodsId < 0 || !StringUtils.hasText(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     * 验证码校验
     * @param user
     * @param goodsId
     * @param result
     * @author hourui
     * @date 2022/12/31 12:08
     * @return boolean
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String result) {
        if(user == null || goodsId == null || goodsId < 0 || !StringUtils.hasText(result)){
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return result.equals(redisCaptcha);
    }
}
