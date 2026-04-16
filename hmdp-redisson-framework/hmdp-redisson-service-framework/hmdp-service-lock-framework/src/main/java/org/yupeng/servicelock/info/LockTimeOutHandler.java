package org.yupeng.servicelock.info;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Failure handling abstraction
 * @author: yupeng
 **/
public interface LockTimeOutHandler {
    
    /**
     * deal with
     * @param lockName lock name
     * */
    void handler(String lockName);
}
