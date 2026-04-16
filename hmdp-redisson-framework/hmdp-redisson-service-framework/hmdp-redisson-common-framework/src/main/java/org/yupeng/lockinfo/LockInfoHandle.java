package org.yupeng.lockinfo;

import org.aspectj.lang.JoinPoint;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Lock information abstraction
 * @author: yupeng
 **/
public interface LockInfoHandle {
   
    String getLockName(JoinPoint joinPoint, String name, String[] keys);
    
    String simpleGetLockName(String name,String[] keys);
}
