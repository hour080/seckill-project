package com.example.seckill.vo;

import lombok.*;

/**
 * 公共返回对象的枚举
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/18 19:58
 */
@ToString
@Getter
@AllArgsConstructor
public enum RespBeanEnum {

    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    //登陆模块
    PASSWORD_ERROR(500210, "密码错误"),
    USER_ERROR(500211, "用户未注册"),
    BIND_ERROR(500212, "参数校验异常"),
    //秒杀模块
    EMPTY_STOCK(500510, "库存不足"),

    REPEAT_ERROR(500511, "该商品每人限购买一件");

    private final Integer CODE;
    private final String MESSAGE;

}
