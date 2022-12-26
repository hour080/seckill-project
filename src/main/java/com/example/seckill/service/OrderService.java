package com.example.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
public interface OrderService extends IService<Order> {

    Order createOrderAndSeckillOrder(User user, GoodsVo goodsVo);
}
