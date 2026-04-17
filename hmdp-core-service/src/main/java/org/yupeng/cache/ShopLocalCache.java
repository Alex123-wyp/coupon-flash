package org.yupeng.cache;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.stereotype.Component;
import org.yupeng.entity.Shop;

import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Local cache: seckill voucher details
 * @author: yupeng
 **/
@Component
public class ShopLocalCache {

    private final Cache<String, Shop> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfter(new Expiry<String, Shop>() {

                /**
                 * There is a stale window: when created voucher is expired, it will still get into logic for 1 second, at this time a user can get an expired voucher,
                 * we are tolerant to this case, but we are strictly check the expired time when user order it;
                 * @param key
                 * @param value
                 * @param currentTime
                 * @return
                 */
                @Override
                public long expireAfterCreate(String key, Shop value, long currentTime) {
                    long ttlSeconds = 60L;
                    return TimeUnit.NANOSECONDS.convert(ttlSeconds, TimeUnit.SECONDS);
                }

                @Override
                public long expireAfterUpdate(String key, Shop value, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(String key, Shop value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
                
            })
            .build();



    public Shop get(String shopId) {
        return cache.getIfPresent(shopId);
    }

    public void put(String shopId, Shop shop) {
        if (shopId != null && shop != null) {
            cache.put(shopId, shop);
        }
    }

    public void invalidate(String voucherId) {
        cache.invalidate(voucherId);
    }
}