package org.yupeng.service;


/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User notification service interface after automatic coupon issuance is successful
 * @author: yupeng
 **/
public interface IAutoIssueNotifyService {
    
    void sendAutoIssueNotify(Long voucherId, Long userId, Long orderId);
}