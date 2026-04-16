package org.yupeng.handler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Factory for retrieving BloomFilterHandler by name.
 * @author: yupeng
 **/
public class BloomFilterHandlerFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public BloomFilterHandler get(String name){
        return applicationContext.getBean(name, BloomFilterHandler.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}