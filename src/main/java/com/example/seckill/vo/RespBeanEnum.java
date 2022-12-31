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

    PASSWORD_UPDATE_FAILED(500213, "更新密码失败"),
    //秒杀模块
    EMPTY_STOCK(500510, "库存不足"),

    REPEAT_ERROR(500511, "该商品每人限购买一件"),

    TIME_ERROR(500512, "超出秒杀时间范围"),

    REQUEST_ILLEGAL(500513, "请求非法"),

    CAPTCHA_ERROR(500514, "验证码错误，请重新输入"),

    ACCESS_LIMIT_REACHED(500515, "访问过于频繁, 请稍候再试"),
    //订单模块
    ORDER_NOT_EXIST(500310, "订单信息不存在");

    private final Integer CODE;
    private final String MESSAGE;

}
