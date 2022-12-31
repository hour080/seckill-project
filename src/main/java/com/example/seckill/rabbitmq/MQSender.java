package com.example.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/28 15:23
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String ROUTING_KEY_MESSAGE = "seckill.message";

    /**
     * 发送秒杀信息
     * @param message
     * @author hourui
     * @date 2022/12/29 17:21
     * @return void
     */
    public void sendSecKillMessage(String message){
        log.info("发送消息:" + message);
        rabbitTemplate.convertAndSend("seckillExchange", ROUTING_KEY_MESSAGE, message);
    }
}
