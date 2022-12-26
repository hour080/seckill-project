package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.mapper.SeckillGoodsMapper;
import com.example.seckill.pojo.SeckillGoods;
import com.example.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {

    @Override
    public List<SeckillGoods> getAllSeckillOfGoodsIds(List<Long> goodsIdList) {
        return this.getBaseMapper().getAllSeckillOfGoodsIds(goodsIdList);
    }
}
