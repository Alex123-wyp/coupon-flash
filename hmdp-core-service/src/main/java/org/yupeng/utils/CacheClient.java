package org.yupeng.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.yupeng.utils.RedisConstants.CACHE_NULL_TTL;
import static org.yupeng.utils.RedisConstants.LOCK_SHOP_KEY;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Cache tool-Using the normal version of Dark Horse Dianping
 * @author: yupeng
 **/
@Slf4j
@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // Set logical expiration
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // Write to Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    public <R,ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit){
        String key = keyPrefix + id;
        // 1. Query the store cache from redis
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2. Determine whether it exists
        if (StrUtil.isNotBlank(json)) {
            // 3. Exists, returns directly
            return JSONUtil.toBean(json, type);
        }
        // Determine whether the hit is a null value
        if (json != null) {
            // Return an error message
            return null;
        }

        // 4. Does not exist, query the database based on id
        R r = dbFallback.apply(id);
        // 5. Does not exist, returns an error
        if (r == null) {
            // Write null values ​​to redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            // Return error message
            return null;
        }
        // 6. Exists and writes to redis
        this.set(key, r, time, unit);
        return r;
    }

    public <R, ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1. Query the store cache from redis
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2. Determine whether it exists
        if (StrUtil.isBlank(json)) {
            // 3. Exists, returns directly
            return null;
        }
        // 4. Hit, you need to deserialize json into an object first
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 5. Determine whether it has expired
        if(expireTime.isAfter(LocalDateTime.now())) {
            // 5.1. Not expired, return to shop information directly
            return r;
        }
        // 5.2. Expired and needs to be cached again.
        // 6. Cache reconstruction
        // 6.1. Obtain mutex lock
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        // 6.2. Determine whether the lock is acquired successfully
        if (isLock){
            // 6.3. Successful, start independent thread to realize cache reconstruction
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // Query database
                    R newR = dbFallback.apply(id);
                    // Rebuild cache
                    this.setWithLogicalExpire(key, newR, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    // release lock
                    unlock(lockKey);
                }
            });
        }
        // 6.4. Return expired store information
        return r;
    }

    public <R, ID> R queryWithMutex(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1. Query the store cache from redis
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2. Determine whether it exists
        if (StrUtil.isNotBlank(shopJson)) {
            // 3. Exists, returns directly
            return JSONUtil.toBean(shopJson, type);
        }
        // Determine whether the hit is a null value
        if (shopJson != null) { 
            // Return an error message
            return null;
        }

        // 4. Implement cache reconstruction
        // 4.1. Obtain mutex lock
        String lockKey = LOCK_SHOP_KEY + id;
        R r = null;
        try {
            boolean isLock = tryLock(lockKey);
            // 4.2. Determine whether the acquisition is successful
            if (!isLock) {
                // 4.3. Failed to acquire lock, sleep and try again
                Thread.sleep(50);
                return queryWithMutex(keyPrefix, id, type, dbFallback, time, unit);
            }
            // 4.4. Obtain the lock successfully and query the database based on the id.
            r = dbFallback.apply(id);
            // 5. Does not exist, returns an error
            if (r == null) {
                // Write null values ​​to redis
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                // Return error message
                return null;
            }
            // 6. Exists and writes to redis
            this.set(key, r, time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 7. Release the lock
            unlock(lockKey);
        }
        // 8.Return
        return r;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
