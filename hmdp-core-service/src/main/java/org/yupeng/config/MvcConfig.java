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
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: CORS configuration
 * @author: yupeng
 **/
@Configuration
//WebMvcConfigurer let you customize SpringMVC behavior
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //Login interceptor
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
//        //Token refresh interceptor
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
