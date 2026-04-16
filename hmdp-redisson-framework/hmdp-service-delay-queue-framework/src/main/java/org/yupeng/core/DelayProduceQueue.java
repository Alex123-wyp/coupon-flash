package org.yupeng.core;

import org.redisson.Redisson;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RReliableQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.PlainOptions;
import org.redisson.client.codec.Codec;

import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Delay queue delay queue
 * @author: yupeng
 **/
public class DelayProduceQueue extends DelayBaseQueue{
    
    /**
     * Although RDelayedQueue is expired here, it is recommended to use {@link RReliableQueue} instead.
     * <p>
     * However, RRelableQueue is a function that can only be used in the pro version. If used in the community version, an "unsupported" exception will be thrown directly:
     * <p>
     * Source code location:
     * <p>
     * <ul>
     *     <li>{@link Redisson#getReliableQueue(String)}</li>
     *     <li>{@link Redisson#getReliableQueue(String, Codec)}</li>
     *     <li>{@link Redisson#getReliableQueue(PlainOptions)}</li>
     * </ul>
     * Official website description:
     * <p>
     * <ul>
     *     <li><a href="https://redisson.pro/feature-comparison.html">https://redisson.pro/feature-comparison.html</a></li>
     *     <li><a href="https://redisson.pro/docs/data-and-services/queues/">https://redisson.pro/docs/data-and-services/queues</a></li>
     * </ul>
     * 
     * Using RDelayedQueue is also enough, because we can use the message reconciliation function to improve reliability, which is what the Damai project does
     * */
    private final RDelayedQueue<String> delayedQueue;
    public DelayProduceQueue(RedissonClient redissonClient, final String relTopic) {
        super(redissonClient, relTopic);
        this.delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
    }
    
    public void offer(String content, long delayTime, TimeUnit timeUnit) {
        delayedQueue.offer(content,delayTime,timeUnit);
    }
}
