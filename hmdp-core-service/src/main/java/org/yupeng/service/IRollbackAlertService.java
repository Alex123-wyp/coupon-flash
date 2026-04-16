package org.yupeng.service;

import org.yupeng.entity.RollbackFailureLog;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Rollback failure notification service for sending SMS/email alerts (pluggable implementation).
 * @author: yupeng
 **/
public interface IRollbackAlertService {

    void sendRollbackAlert(RollbackFailureLog log);
}