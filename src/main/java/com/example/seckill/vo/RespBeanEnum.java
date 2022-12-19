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
    LOGIN_ERROR(500210, "手机号或密码不正确"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    USER_ERROR(500212, "用户未注册");
    private final Integer CODE;
    private final String MESSAGE;

}
