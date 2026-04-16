package org.yupeng.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

import static org.yupeng.config.DelayQueueProperties.PREFIX;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Delay queue configuration properties
 * @author: yupeng
 **/
@Data
@ConfigurationProperties(prefix = PREFIX)
public class DelayQueueProperties {

    public static final String PREFIX = "delay.queue";
    
    /**
     * core thread count in the thread pool that pulls data from the queue，increase this value if the business processing is too slow
     * */
    private Integer corePoolSize = 4;
    /**
     * maximum thread count in the thread pool that pulls data from the queue，increase this value if the business processing is too slow
     * */
    private Integer maximumPoolSize = 4;
    
    /**
     * maximum idle thread keep-alive time in the thread pool that pulls data from the queue
     * */
    private long keepAliveTime = 30;
    /**
     * The time unit of maximum idle thread keep-alive time in the thread pool that pulls data from the queue
     * */
    private TimeUnit unit = TimeUnit.SECONDS;
    /**
     * queue size in the thread pool that pulls data from the queue，increase this value if the business processing is too slow
     * */
    private Integer workQueueSize = 256;
    
    /**
     * number of isolated partitions for the delay queue; increase it when latency becomes a bottleneck, but it will increase Redis CPU usage (producer and consumer partition counts must match for the same topic)
     * */
    private Integer isolationRegionCount = 5;
}
