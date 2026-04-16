package org.yupeng.util;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Distributed lock functional task with a return value
 * @author: yupeng
 **/
@FunctionalInterface
public interface TaskCall<V> {

    /**
     * perform tasks
     * @return result
     * */
    V call();
}
