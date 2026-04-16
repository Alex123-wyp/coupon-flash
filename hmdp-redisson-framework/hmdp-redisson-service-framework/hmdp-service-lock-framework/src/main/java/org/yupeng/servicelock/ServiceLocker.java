package org.yupeng.servicelock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Method abstraction
 * @author: yupeng
 **/
public interface ServiceLocker {
    
    /**
     * Get lock
     * @param lockKey lock key
     * @return result
     * */
    RLock getLock(String lockKey);
    
    /**
     * Lock
     * @param lockKey lock key
     * @return result
     * */
    RLock lock(String lockKey);
    
    /**
     * Lock
     * @param lockKey lock key
     * @param leaseTime release time
     * @return result
     * */
    RLock lock(String lockKey, long leaseTime);
    
    /**
     * Lock
     * @param lockKey lock key
     * @param unit time unit
     * @param leaseTime release time
     * @return result
     * */
    RLock lock(String lockKey, TimeUnit unit, long leaseTime);
    
    /**
     * Lock
     * @param lockKey lock key
     * @param unit time unit
     * @param waitTime waiting time
     * @return result
     * */
    boolean tryLock(String lockKey, TimeUnit unit, long waitTime);
    
    /**
     * Lock
     * @param lockKey lock key
     * @param unit time unit
     * @param waitTime waiting time
     * @param leaseTime release time
     * @return result
     * */
    boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime);
    
    /**
     * Unlock
     * @param lockKey lock key
     * */
    void unlock(String lockKey);
    
    /**
     * Unlock
     * @param lock lock
     * */
    void unlock(RLock lock);
}