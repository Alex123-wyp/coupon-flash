package org.yupeng.ratelimit.extension;

import org.yupeng.enums.BaseCode;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Default no-op implementation
 * @author: yupeng
 **/
public class NoOpRateLimitPenaltyPolicy implements RateLimitPenaltyPolicy {
    @Override
    public void apply(RateLimitContext ctx, BaseCode reason) {
        // no-op
    }
}