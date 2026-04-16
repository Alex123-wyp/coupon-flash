package org.yupeng.config;

import org.yupeng.constant.LockInfoType;
import org.yupeng.core.ManageLocker;
import org.yupeng.lockinfo.LockInfoHandle;
import org.yupeng.lockinfo.factory.LockInfoHandleFactory;
import org.yupeng.lockinfo.impl.ServiceLockInfoHandle;
import org.yupeng.servicelock.aspect.ServiceLockAspect;
import org.yupeng.servicelock.factory.ServiceLockFactory;
import org.yupeng.util.ServiceLockTool;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Configuration
 * @author: yupeng
 **/
public class ServiceLockAutoConfiguration {
    
    @Bean(LockInfoType.SERVICE_LOCK)
    public LockInfoHandle serviceLockInfoHandle(){
        return new ServiceLockInfoHandle();
    }
    
    @Bean
    public ManageLocker manageLocker(RedissonClient redissonClient){
        return new ManageLocker(redissonClient);
    }
    
    @Bean
    public ServiceLockFactory serviceLockFactory(ManageLocker manageLocker){
        return new ServiceLockFactory(manageLocker);
    }
    
    @Bean
    public ServiceLockAspect serviceLockAspect(LockInfoHandleFactory lockInfoHandleFactory,ServiceLockFactory serviceLockFactory){
        return new ServiceLockAspect(lockInfoHandleFactory,serviceLockFactory);
    }
    
    @Bean
    public ServiceLockTool serviceLockTooL(LockInfoHandleFactory lockInfoHandleFactory,ServiceLockFactory serviceLockFactory){
        return new ServiceLockTool(lockInfoHandleFactory,serviceLockFactory);
    }
}
