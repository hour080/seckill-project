package com.example.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义注解参数校验，用于检验手机号是否符合格式
 * @author hourui
 * @date 2022/12/19 11:59
 * @return
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class}) //在validatedBy中定义校验的规则
public @interface IsMobile {
    boolean required() default true; //手机号码必须要填写

    String message() default "手机号码格式错误"; //报错的消息

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
