package org.yupeng.init;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.cache.SeckillVoucherLocalCache;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.entity.SeckillVoucher;
import org.yupeng.entity.Shop;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.ISeckillVoucherService;
import org.yupeng.service.IShopService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Delete cached data
 * @author: yupeng
 **/
@Slf4j
@Order(3)
@Component
public class DelCacheDataInit {
    
    @Resource
    private RedisCache redisCache;
    
    @Resource
    private IShopService shopService;
    
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    
    @Resource
    private SeckillVoucherLocalCache seckillVoucherLocalCache;
    
    @PostConstruct
    public void init(){
        log.info("==========删除缓存中的数据==========");
        List<Shop> shopList = shopService.list();
        for (final Shop shop : shopList) {
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY,shop.getId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY_NULL,shop.getId()));
        }
        List<SeckillVoucher> seckillVoucherList = seckillVoucherService.list();
        for (final SeckillVoucher seckillVoucher : seckillVoucherList) {
            RedisKeyBuild seckillVoucherRedisKey =
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, seckillVoucher.getVoucherId());
            seckillVoucherLocalCache.invalidate(seckillVoucherRedisKey.getRelKey());
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_NULL_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_TRACE_LOG_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_USER_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_ZSET_TAG_KEY,seckillVoucher.getVoucherId()));
            redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY,seckillVoucher.getVoucherId()));

        }
    }
}
