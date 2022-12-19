package com.example.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String mobile;
    private String password;
}
