package com.example.seckill.config;

import com.example.seckill.interceptor.UserInterceptor;
import com.example.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * TODO
 * mvc配置类
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/20 16:36
 */
@Configuration
@EnableWebMvc //全面接管spring mvc, 默认的spring mvc失效
public class MVCConfig implements WebMvcConfigurer {
    @Autowired
    private UserArgumentResolver userArgumentResolver;

    //配置静态资源的路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

    }

    /*
     * 自定义HandlerMethodArgumentResolver参数解析器
     * @param resolvers 参数解析器列表
     * @author hourui
     * @date 2022/12/20 18:28
     * @return
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}
