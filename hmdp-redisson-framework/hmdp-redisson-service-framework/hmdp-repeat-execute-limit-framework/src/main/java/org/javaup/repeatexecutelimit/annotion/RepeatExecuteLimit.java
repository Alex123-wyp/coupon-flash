package org.javaup.repeatexecutelimit.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料 
 * @description: 注解
 * @author: yupeng
 **/
@Target(value= {ElementType.TYPE, ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface RepeatExecuteLimit {
    
    String name() default "";
   
    String [] keys();
    
    long durationTime() default 0L;
    
    String message() default "提交频繁，请稍后重试";
    
}
