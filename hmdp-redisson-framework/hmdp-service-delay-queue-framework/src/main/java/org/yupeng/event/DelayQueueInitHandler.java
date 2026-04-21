package org.yupeng.event;

import cn.hutool.core.collection.CollectionUtil;
import org.yupeng.context.DelayQueueBasePart;
import org.yupeng.context.DelayQueuePart;
import org.yupeng.core.ConsumerTask;
import org.yupeng.core.DelayConsumerQueue;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Event.
 * @author: yupeng
 **/
@AllArgsConstructor
public class DelayQueueInitHandler implements ApplicationListener<ApplicationStartedEvent> {
    
    private final DelayQueueBasePart delayQueueBasePart;
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        /**
         * key: Spring bean name; value: actual ConsumerTask bean object
         *
         */
        Map<String, ConsumerTask> consumerTaskMap = event.getApplicationContext().getBeansOfType(ConsumerTask.class);
        if (CollectionUtil.isEmpty(consumerTaskMap)) {
            return;
        }

        /**
         * consumerTaskMap.values(): Get the real instance of consumer Task
         */
        for (ConsumerTask consumerTask : consumerTaskMap.values()) {
            /**
             * Delay queue part
             */
            DelayQueuePart delayQueuePart = new DelayQueuePart(delayQueueBasePart,consumerTask);
            Integer isolationRegionCount = delayQueuePart.getDelayQueueBasePart().getDelayQueueProperties()
                    .getIsolationRegionCount();

            for(int i = 0; i < isolationRegionCount; i++) {
                DelayConsumerQueue delayConsumerQueue = new DelayConsumerQueue(delayQueuePart, 
                        delayQueuePart.getConsumerTask().topic() + "-" + i);
                delayConsumerQueue.listenStart();

            }

        }
    }
}
