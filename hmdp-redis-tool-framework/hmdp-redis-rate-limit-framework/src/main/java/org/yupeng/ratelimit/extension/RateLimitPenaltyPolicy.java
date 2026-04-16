package org.yupeng.ratelimit.extension;

import org.yupeng.enums.BaseCode;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Penalty strategy extension point: executes actions such as blocking, tagging, or alerting after rate limiting is triggered.
 * @author: yupeng
 **/
public interface RateLimitPenaltyPolicy {

    /**
     * Apply the penalty policy
     * @param ctx    current rate-limit context
     * @param reason trigger reason (IP/user rate limiting, etc.)
     */
    void apply(RateLimitContext ctx, BaseCode reason);
}