package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.mapper.GoodsMapper;
import com.example.seckill.pojo.Goods;
import com.example.seckill.pojo.SeckillGoods;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.SeckillGoodsService;
import com.example.seckill.vo.GoodsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 * @author hourui
 * @since 2022-12-23
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询所有商品信息，两次单表查询实现
     * @author hourui
     * @date 2022/12/24 15:21
     * @return java.util.List<com.example.seckill.vo.GoodsVo>
     */
    public List<GoodsVo> findGoodsVoByMultiSearch() {
        List<Goods> goodsList = this.list();
        if(goodsList == null || goodsList.size() == 0){
            return null;
        }
        List<Long> goodsIdList = goodsList.stream().map(Goods::getId).collect(Collectors.toList());
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.getAllSeckillOfGoodsIds(goodsIdList);
        List<GoodsVo> goodsVoList = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(goods, goodsVo);
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                if(goods.getId().equals(seckillGoods.getGoodsId())){
                    BeanUtils.copyProperties(seckillGoods, goodsVo);
                    break;
                }
            }
            goodsVoList.add(goodsVo);
        }
        return goodsVoList;
    }
    /**
     * 查询所有商品信息，商品表和秒杀商品表关联查询left join实现
     * @author hourui
     * @date 2022/12/24 15:21
     * @return java.util.List<com.example.seckill.vo.GoodsVo>
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return this.getBaseMapper().findGoodsVo();
    }
    /**
     * 获取指定商品详情
     * @param goodsId
     * @author hourui
     * @date 2022/12/24 16:09
     * @return java.lang.String
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        if(goodsId == null) return null;
        return this.getBaseMapper().findGoodsVoByGoodsId(goodsId);
    }
}
