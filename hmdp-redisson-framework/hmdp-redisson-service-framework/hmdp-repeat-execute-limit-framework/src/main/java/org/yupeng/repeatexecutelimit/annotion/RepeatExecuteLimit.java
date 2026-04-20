package org.yupeng.repeatexecutelimit.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Annotation
 * @author: yupeng
 **/
@Target(value= {ElementType.TYPE, ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface RepeatExecuteLimit {
    
    String name() default "";
   
    String [] keys();
    
    long durationTime() default 0L;
    
    String message() default "Submitted too frequently, please try again later";
    
}
