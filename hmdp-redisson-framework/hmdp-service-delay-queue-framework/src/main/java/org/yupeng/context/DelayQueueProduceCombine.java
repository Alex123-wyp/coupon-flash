package org.yupeng.context;

import org.yupeng.core.DelayProduceQueue;
import org.yupeng.core.IsolationRegionSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Delay queue sender fragment selection
 * @author: yupeng
 **/
public class DelayQueueProduceCombine {
    
    private final IsolationRegionSelector isolationRegionSelector;
    
    private final List<DelayProduceQueue> delayProduceQueueList = new ArrayList<>();
    
    public DelayQueueProduceCombine(DelayQueueBasePart delayQueueBasePart,String topic){
        Integer isolationRegionCount = delayQueueBasePart.getDelayQueueProperties().getIsolationRegionCount();
        isolationRegionSelector =new IsolationRegionSelector(isolationRegionCount);
        for(int i = 0; i < isolationRegionCount; i++) {
            delayProduceQueueList.add(new DelayProduceQueue(delayQueueBasePart.getRedissonClient(),topic + "-" + i));
        }
    }
    
    public void offer(String content,long delayTime, TimeUnit timeUnit){
        int index = isolationRegionSelector.getIndex();
        delayProduceQueueList.get(index).offer(content, delayTime, timeUnit);
    }
}
