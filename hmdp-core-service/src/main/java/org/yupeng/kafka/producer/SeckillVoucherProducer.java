package org.yupeng.kafka.producer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.AbstractProducerHandler;
import org.yupeng.enums.SeckillVoucherOrderOperate;
import org.yupeng.kafka.message.SeckillVoucherMessage;
import org.yupeng.kafka.redis.RedisVoucherData;
import org.yupeng.message.MessageExtend;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: Kafka 生产者：发送秒杀券
 * @author: yupeng
 **/
@Slf4j
@Component
public class SeckillVoucherProducer extends AbstractProducerHandler<MessageExtend<SeckillVoucherMessage>> {
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    
    @Resource
    private RedisVoucherData redisVoucherData;
    
    public SeckillVoucherProducer(final KafkaTemplate<String,MessageExtend<SeckillVoucherMessage>> kafkaTemplate) {
        super(kafkaTemplate);
    }
    
    @Override
    protected void afterSendFailure(final String topic, final MessageExtend<SeckillVoucherMessage> message, final Throwable throwable) {
        super.afterSendFailure(topic, message, throwable);
        long traceId = snowflakeIdGenerator.nextId();
        redisVoucherData.rollbackRedisVoucherData(
                SeckillVoucherOrderOperate.YES,
                traceId,
                message.getMessageBody().getVoucherId(),
                message.getMessageBody().getUserId(),
                message.getMessageBody().getOrderId(),
                message.getMessageBody().getAfterQty(),
                message.getMessageBody().getChangeQty(),
                message.getMessageBody().getBeforeQty());
    }
}
