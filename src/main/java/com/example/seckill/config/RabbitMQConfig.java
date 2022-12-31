package com.example.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 * rabbitmq配置类
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/28 15:18
 */
@Configuration
public class RabbitMQConfig {
    private static final String QUEUE_NAME = "seckillQueue";
    private static final String EXCHANGE_NAME = "seckillExchange";

    private static final String ROUTING_KEY = "seckill.#";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with(ROUTING_KEY);
    }

}
