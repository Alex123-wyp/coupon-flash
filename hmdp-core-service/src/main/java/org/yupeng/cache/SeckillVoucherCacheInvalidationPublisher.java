package org.yupeng.cache;

import jakarta.annotation.Resource;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.kafka.message.SeckillVoucherInvalidationMessage;
import org.yupeng.kafka.producer.SeckillVoucherInvalidationProducer;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.core.SpringUtil;
import org.springframework.stereotype.Component;

import static org.yupeng.constant.Constant.SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 业务发布入口：触发秒杀券缓存失效广播
 * @author: yupeng
 **/
@Component
public class SeckillVoucherCacheInvalidationPublisher {

    @Resource
    private RedisCache redisCache;
    
    @Resource
    private SeckillVoucherInvalidationProducer invalidationProducer;
    
    @Resource
    private SeckillVoucherLocalCache seckillVoucherLocalCache;
    
    public void publishInvalidate(Long voucherId, String reason) {
        RedisKeyBuild seckillVoucherRedisKey =
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId);
        seckillVoucherLocalCache.invalidate(seckillVoucherRedisKey.getRelKey());
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId));
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId));
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_NULL_TAG_KEY, voucherId));
        
        SeckillVoucherInvalidationMessage payload = new SeckillVoucherInvalidationMessage(voucherId, reason);
        invalidationProducer.sendPayload(
                SpringUtil.getPrefixDistinctionName() + "-" + SECKILL_VOUCHER_CACHE_INVALIDATION_TOPIC,
                payload
        );
    }
}