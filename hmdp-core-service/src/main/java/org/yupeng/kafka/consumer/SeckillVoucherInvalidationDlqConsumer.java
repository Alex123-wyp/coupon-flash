package org.yupeng.kafka.consumer;

import com.alibaba.fastjson.JSON;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.consumer.AbstractConsumerHandler;
import org.yupeng.kafka.message.SeckillVoucherInvalidationMessage;
import org.yupeng.message.MessageExtend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Flash Coupon Cache Invalidation Broadcast DLQ Consumer
 * @author: yupeng
 **/
@Slf4j
@Component
public class SeckillVoucherInvalidationDlqConsumer extends AbstractConsumerHandler<SeckillVoucherInvalidationMessage> {
    
    @Resource
    private MeterRegistry meterRegistry;
    
    
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

 
    public SeckillVoucherInvalidationDlqConsumer() {
        super(SeckillVoucherInvalidationMessage.class);
    }
    
    @KafkaListener(
            topics = {SPRING_INJECT_PREFIX_DISTINCTION_NAME + "-" + SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC + ".DLQ"},
            groupId = "${prefix.distinction.name:hmdp}-seckill_voucher_cache_invalidation_dlq-${random.uuid}"
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
            log.warn("DLQ message payload is empty or voucherId is missing, uuid={}", message.getUuid());
            safeInc("seckill_invalidation_dlq_replay_skipped", "reason", "invalid_payload");
            return;
        }
        
        safeInc("seckill_invalidation_dlq", "reason", "invalid_payload");

        auditLog.error("SECKILL_INVALIDATION_DLQ | message={}", JSON.toJSONString(message));
    }
    
    private void safeInc(String name, String tagKey, String tagValue) {
        try {
            if (meterRegistry != null) {
                meterRegistry.counter(name, tagKey, tagValue).increment();
            }
        } catch (Exception ignore) {
        }
    }
}
