package org.yupeng.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.cache.SeckillVoucherLocalCache;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.entity.SeckillVoucher;
import org.yupeng.entity.Voucher;
import org.yupeng.handler.BloomFilterHandlerFactory;
import org.yupeng.mapper.SeckillVoucherMapper;
import org.yupeng.model.SeckillVoucherFullModel;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.ISeckillVoucherService;
import org.yupeng.service.IVoucherService;
import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.annotion.ServiceLock;
import org.yupeng.util.ServiceLockTool;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.yupeng.constant.Constant.BLOOM_FILTER_HANDLER_VOUCHER;
import static org.yupeng.constant.DistributedLockConstants.UPDATE_SECKILL_VOUCHER_LOCK;
import static org.yupeng.constant.DistributedLockConstants.UPDATE_SECKILL_VOUCHER_STOCK_LOCK;
import static org.yupeng.utils.RedisConstants.CACHE_NULL_TTL;
import static org.yupeng.utils.RedisConstants.LOCK_SECKILL_VOUCHER_KEY;
import static org.yupeng.utils.RedisConstants.LOCK_SECKILL_VOUCHER_STOCK_KEY;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Seckill voucher interface implementation
 * @author: yupeng
 **/
@Slf4j
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherMapper, SeckillVoucher> implements ISeckillVoucherService {
    
    @Resource
    private ServiceLockTool serviceLockTool;
    
    @Resource
    private RedisCache redisCache;
    
    @Resource
    private BloomFilterHandlerFactory bloomFilterHandlerFactory;

    @Resource
    private SeckillVoucherLocalCache seckillVoucherLocalCache;
    
    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;
    
    @Resource
    private IVoucherService voucherService;
    
    @Override
    @ServiceLock(lockType= LockType.Read,name = UPDATE_SECKILL_VOUCHER_LOCK,keys = {"#voucherId"})
    public SeckillVoucherFullModel queryByVoucherId(Long voucherId) {
        RedisKeyBuild seckillVoucherRedisKey =
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId);
        RedisKeyBuild seckillVoucherNullRedisKey =
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_NULL_TAG_KEY, voucherId);

        //Find in local cache first
        SeckillVoucherFullModel localCacheHit = seckillVoucherLocalCache.get(seckillVoucherRedisKey.getRelKey());

        //find  in local cache, then return directly
        if (Objects.nonNull(localCacheHit)) {
            return localCacheHit;
        }

        //if not find in local cache, then find in redis chache
        SeckillVoucherFullModel seckillVoucherFullModel =
                redisCache.get(seckillVoucherRedisKey, SeckillVoucherFullModel.class);

        //find in redis cache, then put this voucher in the local cache, then return
        if (Objects.nonNull(seckillVoucherFullModel)) {
            seckillVoucherLocalCache.put(seckillVoucherRedisKey.getRelKey(), seckillVoucherFullModel);
            return seckillVoucherFullModel;
        }

        //log info: Find voucher: not find in Redis cache, id: vocherId
        log.info("查询秒杀优惠券 从Redis缓存没有查询到 秒杀优惠券的优惠券id : {}",voucherId);

        //call bloomFilterHandlerFactory if not exist, return RuntimeException
        if (!bloomFilterHandlerFactory.get(BLOOM_FILTER_HANDLER_VOUCHER).contains(String.valueOf(voucherId))) {
            log.info("查询秒杀优惠券 布隆过滤器判断不存在 秒杀优惠券id : {}",voucherId);
            throw new RuntimeException("查询秒杀优惠券不存在");
        }

        //bloom filter said, it may exsit, then find the redis, if not exist, return RuntimeException
        Boolean existResult = redisCache.hasKey(seckillVoucherNullRedisKey);
        if (existResult){
            throw new RuntimeException("查询秒杀优惠券不存在");
        }
        //exist, then get lock
        RLock lock = serviceLockTool.getLock(LockType.Reentrant, LOCK_SECKILL_VOUCHER_KEY, new String[]{String.valueOf(voucherId)});
        lock.lock();
        try {

            //If find voucher full model in redis cache, then record in local cache, and return voucher.
            //The reason why it check redis again after lock is: to avoid high concurrency problem, to avoid that threadA did hit the redis but meanwhile thread B write to redis
            //It is called: Double-Checking after Locking

            seckillVoucherFullModel = redisCache.get(seckillVoucherRedisKey, SeckillVoucherFullModel.class);
            if (Objects.nonNull(seckillVoucherFullModel)) {
                seckillVoucherLocalCache.put(seckillVoucherRedisKey.getRelKey(), seckillVoucherFullModel);
                return seckillVoucherFullModel;
            }

            //If find seckill voucher null redis key exist, then throw RuntimeException
            existResult = redisCache.hasKey(seckillVoucherNullRedisKey);
            if (existResult){
                throw new RuntimeException("查询优惠券不存在");
            }

            //Find in database, if seckillVoucher is null, if it is null, then set seckillVoucherNullRedisKey in redis and return RuntimeException
            SeckillVoucher seckillVoucher = lambdaQuery().eq(SeckillVoucher::getVoucherId,voucherId).one();
            if (Objects.isNull(seckillVoucher)) {
                redisCache.set(seckillVoucherNullRedisKey,
                        "这是一个空值",
                        CACHE_NULL_TTL,
                        TimeUnit.MINUTES);
                throw new RuntimeException("查询秒杀优惠券不存在");
            }

            long ttlSeconds = Math.max(
                    LocalDateTimeUtil.between(LocalDateTimeUtil.now(), seckillVoucher.getEndTime()).getSeconds(),
                    1L
            );

            //Below is the case that voucher exists in database
            //Find local voucher in local database
            Voucher voucher = voucherService.lambdaQuery().eq(Voucher::getId, voucherId).one();

            seckillVoucherFullModel = new SeckillVoucherFullModel();
            BeanUtils.copyProperties(seckillVoucher, seckillVoucherFullModel);

            //create a voucher full model object
            seckillVoucherFullModel.setShopId(voucher.getShopId());
            seckillVoucherFullModel.setStatus(voucher.getStatus());
            seckillVoucherFullModel.setStock(null);

            //Put the voucher full model in redis cache
            redisCache.set(
                    seckillVoucherRedisKey,
                    seckillVoucherFullModel,
                    ttlSeconds,
                    TimeUnit.SECONDS
            );

            //Put voucher full model in local cache
            seckillVoucherLocalCache.put(seckillVoucherRedisKey.getRelKey(), seckillVoucherFullModel);
            return seckillVoucherFullModel;
        }finally {

            //Unlock
            lock.unlock();
        }
    }
    
    @Override
    @ServiceLock(lockType= LockType.Read,name = UPDATE_SECKILL_VOUCHER_STOCK_LOCK,keys = {"#voucherId"})
    public void loadVoucherStock(Long voucherId){
        if (!bloomFilterHandlerFactory.get(BLOOM_FILTER_HANDLER_VOUCHER).contains(String.valueOf(voucherId))) {
            log.info("加载库存 布隆过滤器判断不存在 秒杀优惠券id : {}",voucherId);
            throw new RuntimeException("查询秒杀优惠券不存在");
        }
        String stock = 
                redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId), String.class);
        if (Objects.nonNull(stock)) {
            return;
        }
        RLock lock = serviceLockTool.getLock(LockType.Reentrant, LOCK_SECKILL_VOUCHER_STOCK_KEY, 
                new String[]{String.valueOf(voucherId)});
        lock.lock();
        try {
            stock = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId), String.class);
            if (Objects.nonNull(stock)) {
                return;
            }
            SeckillVoucher seckillVoucher = lambdaQuery().eq(SeckillVoucher::getVoucherId,voucherId).one();
            if (Objects.nonNull(seckillVoucher)) {
                long ttlSeconds = Math.max(
                        LocalDateTimeUtil.between(LocalDateTimeUtil.now(), seckillVoucher.getEndTime()).getSeconds(),
                        1L
                );
                redisCache.set(
                        RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId),
                        String.valueOf(seckillVoucher.getStock()),
                        ttlSeconds,
                        TimeUnit.SECONDS
                );
            }
        }finally {
            lock.unlock();
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rollbackStock(final Long voucherId) {
        return seckillVoucherMapper.rollbackStock(voucherId) > 0;
    }
}
