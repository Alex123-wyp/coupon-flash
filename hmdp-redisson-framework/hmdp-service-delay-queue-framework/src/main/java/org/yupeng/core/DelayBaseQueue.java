package org.yupeng.core;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Delay queue blocking queue
 * @author: yupeng
 **/
@Slf4j
public class DelayBaseQueue {
    
    protected final RedissonClient redissonClient;
    protected final RBlockingQueue<String> blockingQueue;
    
    
    public DelayBaseQueue(RedissonClient redissonClient,String relTopic){
        this.redissonClient = redissonClient;
        this.blockingQueue = redissonClient.getBlockingQueue(relTopic);
    }
}
