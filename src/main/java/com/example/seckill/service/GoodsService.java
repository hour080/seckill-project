package com.example.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.Goods;
import com.example.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hourui
 * @since 2022-12-23
 */
public interface GoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     * @author hourui
     * @date 2022/12/24 12:14
     * @return java.util.List<com.example.seckill.vo.GoodsVo>
     */
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
