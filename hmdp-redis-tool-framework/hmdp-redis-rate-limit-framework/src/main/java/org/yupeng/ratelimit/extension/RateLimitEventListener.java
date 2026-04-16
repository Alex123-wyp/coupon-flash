package org.yupeng.ratelimit.extension;

import org.yupeng.enums.BaseCode;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Rate-limit event listener extension point: used for tracing, auditing, or alerting during rate limiting.
 * @author: yupeng
 **/
public interface RateLimitEventListener {

    /**
     * Callback before script execution (keys and arguments already calculated)
     */
    void onBeforeExecute(RateLimitContext ctx);

    /**
     * Callback when the request is allowed
     */
    void onAllowed(RateLimitContext ctx);

    /**
     * Callback when rate limiting blocks the request (distinguishes IP and user)
     */
    void onBlocked(RateLimitContext ctx, BaseCode reason);
}