package com.example.seckill;

import com.example.seckill.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.example.seckill.mapper")
public class SeckillDemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(SeckillDemoApplication.class, args);
    }

}
