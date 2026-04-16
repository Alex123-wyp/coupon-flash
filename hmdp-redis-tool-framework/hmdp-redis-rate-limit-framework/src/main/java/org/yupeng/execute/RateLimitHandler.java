package org.yupeng.execute;
import org.yupeng.ratelimit.extension.RateLimitScene;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Rate-limit execution interface
 * @author: yupeng
 **/
public interface RateLimitHandler {
   
    void execute(Long voucherId, Long userId, RateLimitScene scene);
}
