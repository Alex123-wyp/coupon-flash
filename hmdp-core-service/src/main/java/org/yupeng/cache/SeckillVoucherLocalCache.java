package org.yupeng.cache;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.yupeng.model.SeckillVoucherFullModel;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Local cache: seckill voucher details
 * @author: yupeng
 **/
@Component
public class SeckillVoucherLocalCache {
    
    private final Cache<String, SeckillVoucherFullModel> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfter(new Expiry<String, SeckillVoucherFullModel>() {

                /**
                 * There is a stale window: when created voucher is expired, it will still get into logic for 1 second, at this time a user can get an expired voucher,
                 * we are tolerant to this case, but we are strictly check the expired time when user order it;
                 * @param key
                 * @param value
                 * @param currentTime
                 * @return
                 */
                @Override
                public long expireAfterCreate(String key, SeckillVoucherFullModel value, long currentTime) {
                    long ttlSeconds = 60L;
                    if (value != null && value.getEndTime() != null) {
                        ttlSeconds = Math.max(
                                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), value.getEndTime()).getSeconds(),
                                1L
                        );
                    }
                    return TimeUnit.NANOSECONDS.convert(ttlSeconds, TimeUnit.SECONDS);
                }
                
                @Override
                public long expireAfterUpdate(String key, SeckillVoucherFullModel value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
                
                @Override
                public long expireAfterRead(String key, SeckillVoucherFullModel value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();


    
    public SeckillVoucherFullModel get(String voucherId) {
        return cache.getIfPresent(voucherId);
    }
    
    public void put(String voucherId, SeckillVoucherFullModel voucher) {
        if (voucherId != null && voucher != null) {
            cache.put(voucherId, voucher);
        }
    }
    
    public void invalidate(String voucherId) {
        cache.invalidate(voucherId);
    }
}