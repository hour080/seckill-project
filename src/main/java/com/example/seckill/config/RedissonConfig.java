package com.example.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 * 使用redisson分布式锁解决一人多单问题
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/30 15:20
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        //配置redis地址以及密码
        config.useSingleServer().setAddress("redis://127.0.0.1:6379")
                .setPassword("1021");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
