package org.yupeng.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.yupeng.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.yupeng.utils.RedisConstants.LOGIN_USER_KEY;
import static org.yupeng.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Token interceptor for the regular and plus HMDP versions
 * @author: yupeng
 **/
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Get the token in the request header
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2. Get users in redis based on TOKEN
        String key  = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

        // 3. Determine whether the user exists
        if (userMap.isEmpty()) {
            return true;
        }
        // 5. Convert the queried hash data to UserDTO
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 6. Exists, save user information to ThreadLocal
        UserHolder.saveUser(userDTO);
        // 7. Refresh the token validity period (set in seconds to avoid Redisson pExpire recursion problems)
        stringRedisTemplate.expire(
                key,
                TimeUnit.SECONDS.convert(LOGIN_USER_TTL, TimeUnit.MINUTES),
                TimeUnit.SECONDS
        );
        // 8. Release
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Remove user
        UserHolder.removeUser();
    }
}
