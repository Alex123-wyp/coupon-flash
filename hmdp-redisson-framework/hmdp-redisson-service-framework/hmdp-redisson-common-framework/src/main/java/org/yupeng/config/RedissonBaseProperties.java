package org.yupeng.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Redisson property configuration
 * @author: yupeng
 **/
@Data
@ConfigurationProperties(prefix = "spring.redis.redisson")
public class RedissonBaseProperties {

    private Integer threads = 16;
    
    private Integer nettyThreads = 32;
    
    private Integer corePoolSize = null;
   
    private Integer maximumPoolSize = null;
    
    private long keepAliveTime = 30;
    
    private TimeUnit unit = TimeUnit.SECONDS;
  
    private Integer workQueueSize = 256; 
}
