package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillGoods;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillGoodsService;
import com.example.seckill.service.SeckillOrderService;
import com.example.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    /**
     * 创建订单以及秒杀订单
     * @param user
     * @param goodsVo
     * @author hourui
     * @date 2022/12/25 17:07
     * @return com.example.seckill.pojo.Order
     */
    @Override
    public Order createOrderAndSeckillOrder(User user, GoodsVo goodsVo) {
        //更新秒杀商品表中的商品库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        seckillGoods.setSeckillStock(seckillGoods.getSeckillStock() - 1);
        seckillGoodsService.updateById(seckillGoods);
        goodsVo.setSeckillStock(seckillGoods.getSeckillStock());
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
        return order;
    }
}
