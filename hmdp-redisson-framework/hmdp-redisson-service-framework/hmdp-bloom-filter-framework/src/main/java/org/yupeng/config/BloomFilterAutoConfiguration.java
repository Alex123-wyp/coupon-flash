package org.yupeng.config;

import org.yupeng.handler.BloomFilterHandlerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Bloom filter configuration
 * @author: yupeng
 **/
@EnableConfigurationProperties(BloomFilterProperties.class)
public class BloomFilterAutoConfiguration {
    
    @Bean
    public BloomFilterHandlerFactory bloomFilterHandlerFactory(){
        return new BloomFilterHandlerFactory();
    }

    @Bean
    public BloomFilterHandlerRegistrar bloomFilterHandlerRegistrar(Environment environment){
        return new BloomFilterHandlerRegistrar(environment);
    }
}
