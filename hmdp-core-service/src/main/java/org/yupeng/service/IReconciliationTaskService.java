package org.yupeng.service;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Reconciliation execution interface
 * @author: yupeng
 **/
public interface IReconciliationTaskService {
    
    void reconciliationTaskExecute();

    /**
     * Delete the Redis inventory key of the specified coupon and trigger on-demand reloading.
     */
    void delRedisStock(Long voucherId);
}
