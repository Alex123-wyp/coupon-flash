package org.yupeng.servicelock;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Distributed lock type
 * @author: yupeng
 **/
public enum LockType {
    /**
     * lock type
     */
    Reentrant,
    
    Fair,
   
    Read,
    
    Write;

    LockType() {
    }

}
