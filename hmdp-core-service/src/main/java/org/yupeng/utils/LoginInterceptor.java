package org.yupeng.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Login validation interceptor
 * @author: yupeng
 **/

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Determine whether interception is needed (whether there is a user in ThreadLocal)
        if (UserHolder.getUser() == null) {
            // No, you need to intercept and set the status code
            response.setStatus(401);
            // intercept
            return false;
        }
        // If there are users, then let it go
        return true;
    }
}
