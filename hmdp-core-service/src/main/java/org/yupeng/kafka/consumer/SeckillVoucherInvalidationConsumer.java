package org.yupeng.kafka.consumer;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.cache.SeckillVoucherLocalCache;
import org.yupeng.consumer.AbstractConsumerHandler;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.kafka.message.SeckillVoucherInvalidationMessage;
import org.yupeng.message.MessageExtend;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.annotion.ServiceLock;
import org.springframework.aop.framework.AopContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static org.yupeng.constant.Constant.SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC;
import static org.yupeng.constant.Constant.SPRING_INJECT_PREFIX_DISTINCTION_NAME;
import static org.yupeng.constant.DistributedLockConstants.UPDATE_SECKILL_VOUCHER_LOCK;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Kafka consumer: receives seckill voucher cache invalidation broadcasts
 * @author: yupeng
 **/
@Slf4j
@Component
public class SeckillVoucherInvalidationConsumer extends AbstractConsumerHandler<SeckillVoucherInvalidationMessage> {

    @Resource
    private SeckillVoucherLocalCache seckillVoucherLocalCache;
    
    @Resource
    private MeterRegistry meterRegistry;


    @Resource
    private RedisCache redisCache;

    public SeckillVoucherInvalidationConsumer() {
        //Call the constructor of parent class AbstractConsumerHandler, passes the SeckillVoucherInvalidationMessage.class into it
        super(SeckillVoucherInvalidationMessage.class);
    }

    /**
     *
     * @param value
     * @param headers Collect all headers from the incoming Kafka message, and put them into Map
     *                 header include things like: Kafka topic, partition, offset, timestamp, key metadata and custom header added by producer
     * @param key     RECEIVED_KEY is Spring Kafka's constant for the received Kafka record key
     * @param acknowledgment
     */
    @KafkaListener(
            topics = {SPRING_INJECT_PREFIX_DISTINCTION_NAME + "-" + SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC},
            groupId = "${prefix.distinction.name:hmdp}-seckill_voucher_cache_invalidation-${random.uuid}"
    )
    public void onMessage(String value,
                          @Headers Map<String, Object> headers,
                          @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                          Acknowledgment acknowledgment) {
        consumeRaw(value, key, headers);
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }
    
    @Override
    protected void doConsume(MessageExtend<SeckillVoucherInvalidationMessage> message) {

        SeckillVoucherInvalidationMessage body = message.getMessageBody();
        if (Objects.isNull(body.getVoucherId())) {
            log.warn("Received cache invalidation message with empty payload or missing voucherId, uuid={}", message.getUuid());
            return;
        }
        Long voucherId = body.getVoucherId();

        /**
         * AopContext.currentProxy() -> returns a proxy bean, and proxy invoke the delCache(voucher Id),
         * this makes @ServiceLock AOP logic is applied
         */
        ((SeckillVoucherInvalidationConsumer) AopContext.currentProxy()).delCache(voucherId);
    }
    
    @ServiceLock(lockType= LockType.Write,name = UPDATE_SECKILL_VOUCHER_LOCK,keys = {"#voucherId"})
    public void delCache(Long voucherId){
        RedisKeyBuild seckillVoucherRedisKey =
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId);
        seckillVoucherLocalCache.invalidate(seckillVoucherRedisKey.getRelKey());
        
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId));
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId));
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_NULL_TAG_KEY, voucherId));
    }
    
    @Override
    protected void afterConsumeFailure(final MessageExtend<SeckillVoucherInvalidationMessage> message, final Throwable throwable) {
        //Call the parent afterConsumeFailure() method in override class
        super.afterConsumeFailure(message, throwable);
        log.warn("Failed to delete Redis cache voucherId={}", message.getMessageBody().getVoucherId(), throwable);
        safeInc(errorTag(throwable));
    }
    
    private void safeInc(String tagValue) {
        try {
            if (meterRegistry != null) {
                meterRegistry.counter("seckill_invalidation_consume_failures", "error", tagValue).increment();
            }
        } catch (Exception ignore) {
        }
    }

    private String errorTag(Throwable t) {
        return t == null ? "unknown" : t.getClass().getSimpleName();
    }
}
