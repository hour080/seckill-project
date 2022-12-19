package com.example.seckill.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/18 21:35
 */
public class ValidatorUtil {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");

    /**
     * 验证手机号是否符合正确的格式
     * @param mobile 手机号
     * @author hourui
     * @date 2022/12/18 21:40
     * @return boolean
     */
    public static boolean isMobile(String mobile){
        if(!StringUtils.hasText(mobile)){
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile).matches();
    }
}
