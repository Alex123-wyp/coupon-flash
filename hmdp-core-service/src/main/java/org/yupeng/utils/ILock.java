package org.yupeng.utils;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Lock-Dark Horse Comments normal version use
 * @author: yupeng
 **/
public interface ILock {

    /**
     * Try to acquire lock
     * @param timeoutSec The timeout period of the lock, which will be automatically released after expiration
     * @return true means the lock acquisition was successful; false means the lock acquisition failed
     */
    boolean tryLock(long timeoutSec);

    /**
     * release lock
     */
    void unlock();
}
