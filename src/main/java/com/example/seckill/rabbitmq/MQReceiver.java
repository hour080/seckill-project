package com.example.seckill.rabbitmq;

import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillMessage;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/28 15:29
 */
@Service
@Slf4j
public class MQReceiver {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;


    /**
     * 下单操作
     * 数据库扣减库存，创建订单
     * @author hourui
     * @date 2022/12/29 17:27
     * @return void
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) throws JsonProcessingException {
        log.info("接受到的消息:" + message);
        SeckillMessage seckillMessage = objectMapper.readValue(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //解决库存不足的问题
        if(goodsVo == null || goodsVo.getSeckillStock() < 1){
            return;
        }
        //解决一人多单的问题
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return;
        }
        orderService.createOrderAndSeckillOrder(user, goodsVo);
    }



}
