package org.yupeng.ratelimit.extension;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Rate-limit scenario
 * @author: yupeng
 **/
public enum RateLimitScene {
    /** Token issuing API */
    ISSUE_TOKEN,
    /** Order placement (seckill) API */
    SECKILL_ORDER
}