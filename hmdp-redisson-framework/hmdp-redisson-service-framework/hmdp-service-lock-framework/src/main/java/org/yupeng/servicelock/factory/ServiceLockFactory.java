package org.yupeng.servicelock.factory;

import org.yupeng.core.ManageLocker;
import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.ServiceLocker;
import lombok.AllArgsConstructor;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Factory
 * @author: yupeng
 **/
@AllArgsConstructor
public class ServiceLockFactory {
    
    private final ManageLocker manageLocker;
    

    public ServiceLocker getLock(LockType lockType){
        ServiceLocker lock;
        switch (lockType) {
            case Fair:
                lock = manageLocker.getFairLocker();
                break;
            case Write:
                lock = manageLocker.getWriteLocker();
                break;
            case Read:
                lock = manageLocker.getReadLocker();
                break;
            default:
                lock = manageLocker.getReentrantLocker();
                break;
        }
        return lock;
    }
}
