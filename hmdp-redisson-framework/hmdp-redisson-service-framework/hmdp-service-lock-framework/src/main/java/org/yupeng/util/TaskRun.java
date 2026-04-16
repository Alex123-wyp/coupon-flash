package org.yupeng.util;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Distributed lock functional task without a return value
 * @author: yupeng
 **/
@FunctionalInterface
public interface TaskRun {
    
    /**
     * perform tasks
     * */
    void run();
}
