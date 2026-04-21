package org.yupeng.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Delay queue sender context
 * @author: yupeng
 **/
public class DelayQueueContext {


    private final DelayQueueBasePart delayQueueBasePart;
    /**
     * The key is the topic, and the value is the processor that sends the message.
     * User Concurrent Hash Map to make thread safe and better performance than lock
     * */
    private final Map<String, DelayQueueProduceCombine> delayQueueProduceCombineMap = new ConcurrentHashMap<>();

    /**
     * Constructor for DelayQueueContext
     * @param delayQueueBasePart
     */
    public DelayQueueContext(DelayQueueBasePart delayQueueBasePart){
        this.delayQueueBasePart = delayQueueBasePart;
    }
    
    public void sendMessage(String topic,String content,long delayTime, TimeUnit timeUnit) {
        DelayQueueProduceCombine delayQueueProduceCombine = delayQueueProduceCombineMap.computeIfAbsent(
                topic, k -> new DelayQueueProduceCombine(delayQueueBasePart,topic));

        delayQueueProduceCombine.offer(content,delayTime,timeUnit);
    }
}
