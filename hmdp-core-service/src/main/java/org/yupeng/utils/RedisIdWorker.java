package org.yupeng.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Redis ID worker for the regular HMDP version
 * @author: yupeng
 **/
@Component
public class RedisIdWorker {
    /**
     * start timestamp
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;
    /**
     * Number of digits in the serial number
     */
    private static final int COUNT_BITS = 32;

    private StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public long nextId(String keyPrefix) {
        // 1. Generate timestamp
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 2. Generate serial number
        // 2.1. Get the current date, accurate to the day
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // 2.2. Self-growth
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);

        // 3. Splice and return
        return timestamp << COUNT_BITS | count;
    }
}
