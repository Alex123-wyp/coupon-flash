package org.yupeng.config;

import org.yupeng.execute.RedisRateLimitHandler;
import org.yupeng.lua.SlidingRateLimitOperate;
import org.yupeng.lua.TokenBucketRateLimitOperate;
import org.yupeng.ratelimit.extension.NoOpRateLimitEventListener;
import org.yupeng.ratelimit.extension.NoOpRateLimitPenaltyPolicy;
import org.yupeng.ratelimit.extension.RateLimitEventListener;
import org.yupeng.ratelimit.extension.RateLimitPenaltyPolicy;
import org.yupeng.ratelimit.extension.ThresholdPenaltyPolicy;
import org.yupeng.redis.RedisCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Bloom filter configuration
 * @author: yupeng
 **/
@EnableConfigurationProperties(SeckillRateLimitConfigProperties.class)
public class RateLimitAutoConfiguration {
    
    @Bean
    public SlidingRateLimitOperate slidingRateLimitOperate(RedisCache redisCache){
        return new SlidingRateLimitOperate(redisCache);
    }
    
    @Bean
    public TokenBucketRateLimitOperate tokenBucketRateLimitOperate(RedisCache redisCache){
        return new TokenBucketRateLimitOperate(redisCache);
    }

    @Bean
    public RateLimitEventListener rateLimitEventListener(){
        return new NoOpRateLimitEventListener();
    }

    @Bean
    public RateLimitPenaltyPolicy rateLimitPenaltyPolicy(SeckillRateLimitConfigProperties seckillRateLimitConfigProperties,
                                                         RedisCache redisCache){
        
        Boolean enable = seckillRateLimitConfigProperties.getEnablePenalty();
        if (Boolean.TRUE.equals(enable)) {
            return new ThresholdPenaltyPolicy(redisCache, seckillRateLimitConfigProperties);
        }
        return new NoOpRateLimitPenaltyPolicy();
    }

    @Bean
    public RedisRateLimitHandler redisRateLimitHandler(SeckillRateLimitConfigProperties seckillRateLimitConfigProperties,
                                                       RedisCache redisCache,
                                                       SlidingRateLimitOperate slidingRateLimitOperate,
                                                       TokenBucketRateLimitOperate tokenBucketRateLimitOperate,
                                                       RateLimitEventListener rateLimitEventListener,
                                                       RateLimitPenaltyPolicy rateLimitPenaltyPolicy) {
        return new RedisRateLimitHandler(
                seckillRateLimitConfigProperties, 
                redisCache,
                slidingRateLimitOperate,
                tokenBucketRateLimitOperate,
                rateLimitEventListener,
                rateLimitPenaltyPolicy
        );
    }
}
