package com.example.seckill.validator;

import com.example.seckill.utils.ValidatorUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * TODO
 * ConstraintValidator泛型的第一个参数是指明使用该检验器进行验证的注解
 * 第二个参数是要验证的数据类型
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/19 15:11
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required = false;
    //获取要验证的字符串是否是必须填写的
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
            return ValidatorUtil.isMobile(value);
        }else{
            //传入的字符串并不是必填的，有可能为空
            if(StringUtils.hasText(value)){
                return ValidatorUtil.isMobile(value);  //使用正则表达式进行匹配
            }else{
                return false;
            }
        }
    }
}
