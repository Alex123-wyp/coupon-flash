package org.yupeng.config;

import jakarta.annotation.Resource;
import org.apache.zookeeper.Login;
import org.yupeng.utils.LoginInterceptor;
import org.yupeng.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 跨域配置
 * @author: yupeng
 **/
@Configuration
//WebMvcConfigurer let you customize SpringMVC behavior
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 登录拦截器
//        registry.addInterceptor(new LoginInterceptor())
//                .excludePathPatterns(
//                        "/shop/**",
//                        "/voucher/**",
//                        "/shop-type/**",
//                        "/upload/**",
//                        "/blog/hot",
//                        "/user/code",
//                        "/user/login"
//                ).order(1);
//        // token刷新的拦截器
//        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
//    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //Login Interceptors
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                ).order(1);

        // token refresh interceptor
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }
}
