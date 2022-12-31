package com.example.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 放入到RabbitMQ中的秒杀信息类，用于异步扣减库存，生成订单
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/29 16:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodId;
}
