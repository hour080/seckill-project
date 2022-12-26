package com.example.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.SeckillGoods;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
public interface SeckillGoodsService extends IService<SeckillGoods> {
    /**
     * 批查询所有商品的秒杀信息
     * @param goodsIdList
     * @author hourui
     * @date 2022/12/24 14:51
     * @return java.util.List<com.example.seckill.pojo.SeckillGoods>
     */
    List<SeckillGoods> getAllSeckillOfGoodsIds(List<Long> goodsIdList);
}
