package org.yupeng.core;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static org.yupeng.constant.Constant.DEFAULT_PREFIX_DISTINCTION_NAME;
import static org.yupeng.constant.Constant.PREFIX_DISTINCTION_NAME;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Spring utilities
 * @author: yupeng
 **/
public class SpringUtil implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    private static ConfigurableApplicationContext configurableApplicationContext;
    
    
    public static String getPrefixDistinctionName(){
        return configurableApplicationContext.getEnvironment().getProperty(PREFIX_DISTINCTION_NAME,
                DEFAULT_PREFIX_DISTINCTION_NAME);
    }
    
    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        configurableApplicationContext = applicationContext;
    }
}
