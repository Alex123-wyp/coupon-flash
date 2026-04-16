package org.yupeng.core;

import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.ServiceLocker;
import org.yupeng.servicelock.impl.RedissonFairLocker;
import org.yupeng.servicelock.impl.RedissonReadLocker;
import org.yupeng.servicelock.impl.RedissonReentrantLocker;
import org.yupeng.servicelock.impl.RedissonWriteLocker;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

import static org.yupeng.servicelock.LockType.Fair;
import static org.yupeng.servicelock.LockType.Read;
import static org.yupeng.servicelock.LockType.Reentrant;
import static org.yupeng.servicelock.LockType.Write;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Cache
 * @author: yupeng
 **/
public class ManageLocker {

    private final Map<LockType, ServiceLocker> cacheLocker = new HashMap<>();
    
    public ManageLocker(RedissonClient redissonClient){
        cacheLocker.put(Reentrant,new RedissonReentrantLocker(redissonClient));
        cacheLocker.put(Fair,new RedissonFairLocker(redissonClient));
        cacheLocker.put(Write,new RedissonWriteLocker(redissonClient));
        cacheLocker.put(Read,new RedissonReadLocker(redissonClient));
    }
    
    public ServiceLocker getReentrantLocker(){
        return cacheLocker.get(Reentrant);
    }
    
    public ServiceLocker getFairLocker(){
        return cacheLocker.get(Fair);
    }
    
    public ServiceLocker getWriteLocker(){
        return cacheLocker.get(Write);
    }
    
    public ServiceLocker getReadLocker(){
        return cacheLocker.get(Read);
    }
}
