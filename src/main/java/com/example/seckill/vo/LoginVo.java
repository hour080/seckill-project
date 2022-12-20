package com.example.seckill.vo;

import com.example.seckill.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * vo 值对象(Value Object)
 * 主要是在控制层Controller与视图层View进行传输交换的对象！
 * 也就是用于接受前端界面传递来的数据, 或者将数据封装好响应给前端界面。
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/18 21:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {
    @NotBlank //字符串不能为null，并且字符串必须有至少一个非空字符
    @IsMobile
    private String mobile;

    @NotBlank
    @Length(min = 32) //MD5加密以后均为32位
    private String password;
}
