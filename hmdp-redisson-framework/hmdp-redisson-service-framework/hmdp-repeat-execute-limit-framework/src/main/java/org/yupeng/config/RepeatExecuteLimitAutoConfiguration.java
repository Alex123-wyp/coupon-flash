package org.yupeng.config;

import org.yupeng.constant.LockInfoType;
import org.yupeng.handle.RedissonDataHandle;
import org.yupeng.locallock.LocalLockCache;
import org.yupeng.lockinfo.LockInfoHandle;
import org.yupeng.lockinfo.factory.LockInfoHandleFactory;
import org.yupeng.lockinfo.impl.RepeatExecuteLimitLockInfoHandle;
import org.yupeng.repeatexecutelimit.aspect.RepeatExecuteLimitAspect;
import org.yupeng.servicelock.factory.ServiceLockFactory;
import org.springframework.context.annotation.Bean;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料 
 * @description: 配置
 * @author: yupeng
 **/
public class RepeatExecuteLimitAutoConfiguration {
    
    @Bean(LockInfoType.REPEAT_EXECUTE_LIMIT)
    public LockInfoHandle repeatExecuteLimitHandle(){
        return new RepeatExecuteLimitLockInfoHandle();
    }
    
    @Bean
    public RepeatExecuteLimitAspect repeatExecuteLimitAspect(LocalLockCache localLockCache,
                                                             LockInfoHandleFactory lockInfoHandleFactory,
                                                             ServiceLockFactory serviceLockFactory,
                                                             RedissonDataHandle redissonDataHandle){
        return new RepeatExecuteLimitAspect(localLockCache, lockInfoHandleFactory,serviceLockFactory,redissonDataHandle);
    }
}
    