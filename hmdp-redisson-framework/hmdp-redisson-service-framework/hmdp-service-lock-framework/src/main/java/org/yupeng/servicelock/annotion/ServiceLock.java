package org.yupeng.servicelock.annotion;

import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.info.LockTimeOutStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Annotation
 * @author: yupeng
 **/
@Target(value= {ElementType.TYPE, ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ServiceLock {
    
    LockType lockType() default LockType.Reentrant;
    
    String name() default "";
   
    String [] keys();
    
    long waitTime() default 10;
    
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    LockTimeOutStrategy lockTimeoutStrategy() default LockTimeOutStrategy.FAIL;
    
    String customLockTimeoutStrategy() default "";
}
