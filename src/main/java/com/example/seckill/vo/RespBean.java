package com.example.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 * 泛型类中的静态方法不能使用类的泛型
 * 在java中泛型只是一个占位符，必须在传递类型后才能使用
 * 就泛型而言，类实例化时才能真正传递类型参数。
 * 由于静态方法随着类的加载而加载, 先于类的实例化，也就是说类中的泛型还没有传递,真正的类型参数静态的方法就已经加载完成了
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/18 19:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private Integer code;
    private String message;
    private Object obj;

    /**
     * 成功的返回结果
     * @author hourui
     * @date 2022/12/18 20:46
     * @return com.example.seckill.vo.RespBean
     */
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCODE(), RespBeanEnum.SUCCESS.getMESSAGE(), null);
    }
    /**
     * 成功的带对象的返回结果
     * @param obj  附带的对象
     * @author hourui
     * @date 2022/12/18 20:46
     * @return com.example.seckill.vo.RespBean
     */
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCODE(), RespBeanEnum.SUCCESS.getMESSAGE(), obj);
    }
    /**
     * 失败的返回结果
     * @param respBeanEnum 失败的枚举类型，例如500，404
     * @author hourui
     * @date 2022/12/18 20:47
     * @return com.example.seckill.vo.RespBean
     */
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCODE(), respBeanEnum.getMESSAGE(), null);
    }
    /**
     * 失败的带有对象的返回结果
     * @param respBeanEnum  失败的枚举类型，例如500，404
     * @param obj  附带的对象
     * @author hourui
     * @date 2022/12/18 20:49
     * @return com.example.seckill.vo.RespBean
     */
    public static RespBean error(RespBeanEnum respBeanEnum, Object obj){
        return new RespBean(respBeanEnum.getCODE(), respBeanEnum.getMESSAGE(), obj);
    }

}
