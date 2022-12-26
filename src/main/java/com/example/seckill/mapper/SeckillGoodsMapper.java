package com.example.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckill.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
    List<SeckillGoods> getAllSeckillOfGoodsIds(@Param("goodsIdList") List<Long> goodsIdList);
}
