package com.example.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultReactiveScriptExecutor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类, 默认是将RedisConfig的代理类对象存放到容器中
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/20 12:41
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义redisTemplate，其中redisTemplate是springboot-data-redis将操作命令封装成模版，
     * stringRedisTemplate中key和value的序列化方式都是字符串序列化，因此key和value必须是字符串形式
     * @param redisConnectionFactory 连接工厂
     * @author hourui
     * @date 2022/12/20 12:49
     * @return org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置键的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置值的序列化，默认是JDK序化，直接将对象转为stream流，进而转为字节数组。
        //而GenericJackson2JsonRedisSerializer是将对象转为json字符串，再将json字符串序列化为字节数组
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //Hash对象中key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //Hash对象中value序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
    @Bean
    public DefaultRedisScript<Long> script(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        //lock.lua脚本位置，处于类加载文件
        redisScript.setLocation(new ClassPathResource("stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
