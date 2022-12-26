package com.example.seckill.vo;

import com.example.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 前端展示端商品对象
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/23 21:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo {
    //商品id
    private Long id;
    //秒杀价格
    private BigDecimal seckillPrice;

    //秒杀库存数量
    private Integer seckillStock;

    //秒杀开始时间
    private Date startDate;

    //秒杀结束时间
    private Date endDate;

    private String goodsName;

    private String goodsImg;

    //商品原价
    private BigDecimal goodsPrice;

}
