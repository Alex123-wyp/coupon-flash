package org.yupeng.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.cache.SeckillVoucherCacheInvalidationPublisher;
import org.yupeng.context.DelayQueueContext;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.core.SpringUtil;
import org.yupeng.delay.message.DelayedVoucherReminderMessage;
import org.yupeng.dto.DelayVoucherReminderDto;
import org.yupeng.dto.Result;
import org.yupeng.dto.SeckillVoucherDto;
import org.yupeng.dto.UpdateSeckillVoucherDto;
import org.yupeng.dto.UpdateSeckillVoucherStockDto;
import org.yupeng.dto.VoucherDto;
import org.yupeng.dto.VoucherSubscribeBatchDto;
import org.yupeng.dto.VoucherSubscribeDto;
import org.yupeng.entity.SeckillVoucher;
import org.yupeng.entity.Voucher;
import org.yupeng.enums.BaseCode;
import org.yupeng.enums.StockUpdateType;
import org.yupeng.enums.SubscribeStatus;
import org.yupeng.exception.HmdpFrameException;
import org.yupeng.handler.BloomFilterHandlerFactory;
import org.yupeng.mapper.VoucherMapper;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.ISeckillVoucherService;
import org.yupeng.service.IVoucherOrderService;
import org.yupeng.service.IVoucherService;
import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.annotion.ServiceLock;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.yupeng.utils.UserHolder;
import org.yupeng.vo.GetSubscribeStatusVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.yupeng.constant.Constant.BLOOM_FILTER_HANDLER_VOUCHER;
import static org.yupeng.constant.Constant.DELAY_VOUCHER_REMINDER;
import static org.yupeng.constant.DistributedLockConstants.UPDATE_SECKILL_VOUCHER_LOCK;
import static org.yupeng.constant.DistributedLockConstants.UPDATE_SECKILL_VOUCHER_STOCK_LOCK;
import static org.yupeng.service.impl.VoucherOrderServiceImpl.SECKILL_ORDER_EXECUTOR;
import static org.yupeng.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher interface implementation
 * @author: yupeng
 **/
@Slf4j
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    @Resource
    private BloomFilterHandlerFactory bloomFilterHandlerFactory;
    
    @Resource
    private RedisCache redisCache;
    
    @Resource
    private SeckillVoucherCacheInvalidationPublisher seckillVoucherCacheInvalidationPublisher;
    
    @Resource
    private IVoucherOrderService voucherOrderService;
    
    @Resource
    private DelayQueueContext delayQueueContext;

    @Value("${seckill.reminder.ahead.seconds:120}")
    private long reminderAheadSeconds;
    
    @Override
    public Long addVoucher(VoucherDto voucherDto) {

        //Get the current voucher with the largest voucher ID
        Voucher one = lambdaQuery().orderByDesc(Voucher::getId).one();
        //Set the voucher id = 1 by default
        long newId = 1L;
        //If the table is empty, then the default new voucher id = 1; If not, the new voucher id = one + 1
        if (one != null) {
            newId = one.getId() + 1;
        }
        //Save new voucher to the database
        Voucher voucher = new Voucher();
        BeanUtil.copyProperties(voucherDto, voucher);
        voucher.setId(newId);
        save(voucher);

        //Add new voucher to the bloomFilter
        bloomFilterHandlerFactory.get(BLOOM_FILTER_HANDLER_VOUCHER).add(voucher.getId().toString());
        return voucher.getId();
    }


    @Override
    public Result<List<Voucher>> queryVoucherOfShop(Long shopId) {
        // Query voucher information
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        // Return results
        return Result.ok(vouchers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSeckillVoucher(SeckillVoucherDto seckillVoucherDto) {
        //return doAddSeckillVoucherV1(seckillVoucherDto);
        return doAddSeckillVoucherV2(seckillVoucherDto);
    }
    
    @Override
    @ServiceLock(lockType= LockType.Write,name = UPDATE_SECKILL_VOUCHER_LOCK,keys = {"#updateSeckillVoucherDto.voucherId"})
    @Transactional(rollbackFor = Exception.class)
    public void updateSeckillVoucher(UpdateSeckillVoucherDto updateSeckillVoucherDto) {
        Long voucherId = updateSeckillVoucherDto.getVoucherId();
        boolean updatedVoucher = false;
        var voucherUpdate = this.lambdaUpdate().eq(Voucher::getId, voucherId);
        if (updateSeckillVoucherDto.getTitle() != null) {
            voucherUpdate.set(Voucher::getTitle, updateSeckillVoucherDto.getTitle());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getSubTitle() != null) {
            voucherUpdate.set(Voucher::getSubTitle, updateSeckillVoucherDto.getSubTitle());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getRules() != null) {
            voucherUpdate.set(Voucher::getRules, updateSeckillVoucherDto.getRules());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getPayValue() != null) {
            voucherUpdate.set(Voucher::getPayValue, updateSeckillVoucherDto.getPayValue());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getActualValue() != null) {
            voucherUpdate.set(Voucher::getActualValue, updateSeckillVoucherDto.getActualValue());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getType() != null) {
            voucherUpdate.set(Voucher::getType, updateSeckillVoucherDto.getType());
            updatedVoucher = true;
        }
        if (updateSeckillVoucherDto.getStatus() != null) {
            voucherUpdate.set(Voucher::getStatus, updateSeckillVoucherDto.getStatus());
            updatedVoucher = true;
        }
        if (updatedVoucher) {
            voucherUpdate.set(Voucher::getUpdateTime, LocalDateTimeUtil.now()).update();
        }
        
        boolean updatedSeckill = false;
        var seckillUpdate = seckillVoucherService.lambdaUpdate().eq(SeckillVoucher::getVoucherId, voucherId);
        if (updateSeckillVoucherDto.getBeginTime() != null) {
            seckillUpdate.set(SeckillVoucher::getBeginTime, updateSeckillVoucherDto.getBeginTime());
            updatedSeckill = true;
        }
        if (updateSeckillVoucherDto.getEndTime() != null) {
            seckillUpdate.set(SeckillVoucher::getEndTime, updateSeckillVoucherDto.getEndTime());
            updatedSeckill = true;
        }
        if (updateSeckillVoucherDto.getAllowedLevels() != null) {
            seckillUpdate.set(SeckillVoucher::getAllowedLevels, updateSeckillVoucherDto.getAllowedLevels());
            updatedSeckill = true;
        }
        if (updateSeckillVoucherDto.getMinLevel() != null) {
            seckillUpdate.set(SeckillVoucher::getMinLevel, updateSeckillVoucherDto.getMinLevel());
            updatedSeckill = true;
        }
        if (updatedSeckill) {
            seckillUpdate.set(SeckillVoucher::getUpdateTime, LocalDateTimeUtil.now()).update();
        }

        //If there has some updates in terms of both normal voucher and secKill voucher
        if (updatedVoucher || updatedSeckill) {
            voucherUpdate.update();
            seckillUpdate.update();
            seckillVoucherCacheInvalidationPublisher.publishInvalidate(voucherId, "update");
        }
    }
    
    @Override
    @ServiceLock(lockType= LockType.Write,name = UPDATE_SECKILL_VOUCHER_STOCK_LOCK,keys = {"#updateSeckillVoucherDto.voucherId"})
    @Transactional(rollbackFor = Exception.class)
    public void updateSeckillVoucherStock(UpdateSeckillVoucherStockDto updateSeckillVoucherDto) {


        //Find the secKillVoucher that need to update
        SeckillVoucher seckillVoucher = seckillVoucherService.lambdaQuery()
                .eq(SeckillVoucher::getVoucherId, updateSeckillVoucherDto.getVoucherId()).one();
        if (Objects.isNull(seckillVoucher)) {
            throw new HmdpFrameException(BaseCode.SECKILL_VOUCHER_NOT_EXIST);
        }
        //oldStock: the current stock
        Integer oldStock = seckillVoucher.getStock();
        //oldInitStock: get current init stock
        Integer oldInitStock = seckillVoucher.getInitStock();
        //newInitStock: get the init stock the seller want to set
        Integer newInitStock = updateSeckillVoucherDto.getInitStock();

        //If newInit == old stock, return directly
        int changeStock = newInitStock - oldInitStock;
        if (changeStock == 0) {
            return;
        }
        //new stock: the number of stock the seller wants to add
        int newStock = oldStock + changeStock;
        //The seller can not deduct stock end with negative number
        if (newStock < 0 ) {
            throw new HmdpFrameException(BaseCode.AFTER_SECKILL_VOUCHER_REMAIN_STOCK_NOT_NEGATIVE_NUMBER);
        }

        StockUpdateType stockUpdateType = StockUpdateType.INCREASE;
        if (changeStock < 0) {
            stockUpdateType = StockUpdateType.DECREASE;
        }

        seckillVoucherService.lambdaUpdate()
                .set(SeckillVoucher::getStock, newStock)
                .set(SeckillVoucher::getInitStock, newInitStock)
                .set(SeckillVoucher::getUpdateTime, LocalDateTimeUtil.now())
                .eq(SeckillVoucher::getVoucherId, seckillVoucher.getVoucherId())
                .update();
        //get the old voucher stock in the redis.
        String oldRedisStockStr = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, 
                updateSeckillVoucherDto.getVoucherId()), String.class);

        Integer newRedisStock = null;

        //If old voucher stock does not exist in redis(expired), then create another SECKILL_STOCK_TAG_KEY
        if (StrUtil.isBlank(oldRedisStockStr)) {
            redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY,
                    updateSeckillVoucherDto.getVoucherId()),String.valueOf(newInitStock));
        }
        //If the SECKILL_STOCK_TAG_KEY exists in redis, then:
        else {
            int oldRedisStock = Integer.parseInt(oldRedisStockStr);
            newRedisStock = oldRedisStock + changeStock;
            if (newRedisStock < 0 ) {
                throw new HmdpFrameException(BaseCode.AFTER_SECKILL_VOUCHER_REMAIN_STOCK_NOT_NEGATIVE_NUMBER);
            }
            redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY,
                    updateSeckillVoucherDto.getVoucherId()),String.valueOf(newRedisStock));
        }
        log.info("Stock updated successfully! updateType={}, before: dbInitialStock={}, oldRedisStock={}, after: dbInitialStock={}, newRedisStock={}",
                stockUpdateType.getMsg(),
                oldInitStock,
                StrUtil.isBlank(oldRedisStockStr) ? null : oldRedisStockStr,
                newInitStock,
                newRedisStock
                );

        //If you are adding inventory, try to automatically assign qualifications to the oldest unpurchased user in the subscription queue.
        if (stockUpdateType == StockUpdateType.INCREASE) {
            SECKILL_ORDER_EXECUTOR.execute(() -> voucherOrderService
                    .autoIssueVoucherToEarliestSubscriber(seckillVoucher.getVoucherId(),null));
        }
    }
    
    @Override
    public void subscribe(final VoucherSubscribeDto voucherSubscribeDto) {
//        Long voucherId = voucherSubscribeDto.getVoucherId();
//        Long userId = UserHolder.getUser().getId();
//        String userIdStr = String.valueOf(userId);
//
//        //Calculate unified TTL (seconds to expire)
//        Long ttlSeconds = redisCache.getExpire(
//                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
//                TimeUnit.SECONDS
//        );
//        if (Objects.isNull(ttlSeconds) || ttlSeconds <= 0) {
//
//            //Find the seckill voucher from the database
//            SeckillVoucher sv = seckillVoucherService.lambdaQuery()
//                    .eq(SeckillVoucher::getVoucherId, voucherId)
//                    .one();
//            //If sv exists and sv end time exists: then set the ttlSeconds
//            if (Objects.nonNull(sv) && Objects.nonNull(sv.getEndTime())) {
//                ttlSeconds = Math.max(
//                        LocalDateTimeUtil.between(LocalDateTimeUtil.now(), sv.getEndTime()).getSeconds(),
//                        1L
//                );
//            } else {
//                ttlSeconds = 3600L;
//            }
//        }
//        //Check whether the purchase has been made and determine whether the user is in the SECKILL_USER_TAG_KEY:{voucherId} collection (purchased collection)
//        boolean purchased = Boolean.TRUE.equals(redisCache.isMemberForSet(
//                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId),
//                userIdStr
//        ));
//
//
//        RedisKeyBuild statusKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY, voucherId);
//        if (purchased) {
//            redisCache.putHash(statusKey, userIdStr, SubscribeStatus.SUCCESS.getCode(), ttlSeconds, TimeUnit.SECONDS);
//            redisCache.expire(statusKey, ttlSeconds, TimeUnit.SECONDS);
//            return;
//        }
//
//        // Add subscription set (SET), idempotent
//        RedisKeyBuild setKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_USER_TAG_KEY, voucherId);
//        Long added = redisCache.addForSet(setKey, userIdStr);
//        redisCache.expire(setKey, ttlSeconds, TimeUnit.SECONDS);
//
//        // Join subscription queue (ZSET), only write sequential score on first join
//        RedisKeyBuild zsetKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_ZSET_TAG_KEY, voucherId);
//        if (Objects.nonNull(added) && added > 0) {
//            redisCache.addForSortedSet(zsetKey, userIdStr, (double) System.currentTimeMillis(), ttlSeconds, TimeUnit.SECONDS);
//        } else {
//            // If already exists, only align TTL
//            redisCache.expire(zsetKey, ttlSeconds, TimeUnit.SECONDS);
//        }
//
//        // Update the subscription status to SUBSCRIBED (if it is already SUCCESS, it will not be downgraded)
//        Integer prev = redisCache.getForHash(statusKey, userIdStr, Integer.class);
//        if (!SubscribeStatus.SUCCESS.getCode().equals(prev)) {
//            redisCache.putHash(statusKey, userIdStr, SubscribeStatus.SUBSCRIBED.getCode(), ttlSeconds, TimeUnit.SECONDS);
//        }
//        redisCache.expire(statusKey, ttlSeconds, TimeUnit.SECONDS);

        //Get basic information
        Long voucherId = voucherSubscribeDto.getVoucherId();
        Long userId = UserHolder.getUser().getId();
        String userIdStr = String.valueOf(userId);

        //Get the voucher info first
        Long ttlSecond = redisCache.getExpire(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId), TimeUnit.SECONDS);
        if (Objects.isNull(ttlSecond) || ttlSecond <= 0) {
            //Find the voucher from the local database
            SeckillVoucher sv = seckillVoucherService.lambdaQuery()
                    .eq(SeckillVoucher::getVoucherId, voucherId)
                    .one();
            if (Objects.nonNull(sv) && Objects.nonNull(sv.getEndTime())) {
                Long Duration = LocalDateTimeUtil.between(LocalDateTimeUtil.now(), sv.getEndTime()).getSeconds();
                ttlSecond = Math.max(Duration, 1L);
            } else {
                ttlSecond = 3600L;
            }
        }
        //Check whether the purchase has already been made and whether the user has already in the purchased collection
        boolean purchased = Boolean.TRUE.equals(redisCache.isMemberForSet(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId), userIdStr));
        RedisKeyBuild statusKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY, voucherId);
        if (purchased) {
            redisCache.putHash(statusKey, userIdStr, SubscribeStatus.SUCCESS.getCode(), ttlSecond, TimeUnit.SECONDS);
            redisCache.expire(statusKey, ttlSecond, TimeUnit.SECONDS);
            return;
        }
        //Add use to subscribe set idempotent
        RedisKeyBuild setKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_USER_TAG_KEY, voucherId);
        Long added = redisCache.addForSet(setKey, userIdStr);
        redisCache.expire(setKey, ttlSecond, TimeUnit.SECONDS);

        //Join subscription queue, add to zset (send message based on the queue)
        RedisKeyBuild zsetKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_ZSET_TAG_KEY, voucherId);
        if (Objects.nonNull(added) && added > 0) {
            redisCache.addForSortedSet(zsetKey, userIdStr, (double) System.currentTimeMillis(), ttlSecond, TimeUnit.SECONDS);
        } else {
            //If already exists, only update expire time
            redisCache.expire(zsetKey, ttlSecond, TimeUnit.SECONDS);
        }
        //Update status to SUBSCRIBED
        Integer prev = redisCache.getForHash(statusKey, userIdStr, Integer.class);
        if(!SubscribeStatus.SUCCESS.getCode().equals(prev)){
            redisCache.putHash(statusKey, userIdStr, SubscribeStatus.SUBSCRIBED.getCode(), ttlSecond, TimeUnit.SECONDS);
        }
    }
    @Override
    public void unsubscribe(final VoucherSubscribeDto voucherSubscribeDto) {
        Long voucherId = voucherSubscribeDto.getVoucherId();
        Long userId = UserHolder.getUser().getId();
        String userIdStr = String.valueOf(userId);

        RedisKeyBuild setKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_USER_TAG_KEY, voucherId);
        RedisKeyBuild zsetKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_ZSET_TAG_KEY, voucherId);
        RedisKeyBuild statusKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY, voucherId);
        
        // Remove from subscription collections and queues
        redisCache.removeForSet(setKey, userIdStr);
        redisCache.delForSortedSet(zsetKey, userIdStr);
        
        // If purchased, maintain SUCCESS, otherwise set to UNSUBSCRIBED
        boolean purchased = Boolean.TRUE.equals(redisCache.isMemberForSet(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId),
                userIdStr
        ));
        Long ttlSeconds = redisCache.getExpire(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                TimeUnit.SECONDS
        );
        if (ttlSeconds == null || ttlSeconds <= 0) {
            ttlSeconds = 3600L;
        }
        redisCache.putHash(
                statusKey, 
                userIdStr,
                purchased ? SubscribeStatus.SUCCESS.getCode() : SubscribeStatus.UNSUBSCRIBED.getCode(),
                ttlSeconds, TimeUnit.SECONDS);
        redisCache.expire(statusKey, ttlSeconds, TimeUnit.SECONDS);
    }
    
    @Override
    public Integer getSubscribeStatus(final VoucherSubscribeDto voucherSubscribeDto) {
        Long voucherId = voucherSubscribeDto.getVoucherId();
        Long userId = UserHolder.getUser().getId();
        String userIdStr = String.valueOf(userId);

        RedisKeyBuild statusKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY, voucherId);
        Integer st = redisCache.getForHash(statusKey, userIdStr, Integer.class);
        if (st != null) {
            return st;
        }
        
        boolean purchased = Boolean.TRUE.equals(redisCache.isMemberForSet(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId),
                userIdStr
        ));
        if (purchased) {
            Long ttlSeconds = redisCache.getExpire(
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                    TimeUnit.SECONDS
            );
            if (ttlSeconds == null || ttlSeconds <= 0) {
                ttlSeconds = 3600L;
            }
            redisCache.putHash(statusKey, userIdStr, SubscribeStatus.SUCCESS.getCode(), ttlSeconds, TimeUnit.SECONDS);
            redisCache.expire(statusKey, ttlSeconds, TimeUnit.SECONDS);
            return SubscribeStatus.SUCCESS.getCode();
        }
        
        boolean inQueue = Boolean.TRUE.equals(redisCache.isMemberForSet(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_USER_TAG_KEY, voucherId),
                userIdStr
        ));
        return inQueue ? SubscribeStatus.SUBSCRIBED.getCode() : SubscribeStatus.UNSUBSCRIBED.getCode();
    }
    
    @Override
    public List<GetSubscribeStatusVo> getSubscribeStatusBatch(final VoucherSubscribeBatchDto voucherSubscribeBatchDto) {
        Long userId = UserHolder.getUser().getId();
        String userIdStr = String.valueOf(userId);
        List<GetSubscribeStatusVo> res = new ArrayList<>();
        for (Long voucherId : voucherSubscribeBatchDto.getVoucherIdList()) {
            // Prioritize HASH caching
            RedisKeyBuild statusKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY, voucherId);
            Integer st = redisCache.getForHash(statusKey, userIdStr, Integer.class);
            if (st != null) {
                res.add(new GetSubscribeStatusVo(voucherId, st));
                continue;
            }
            boolean purchased = Boolean.TRUE.equals(redisCache.isMemberForSet(
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId),
                    userIdStr
            ));
            if (purchased) {
                Long ttlSeconds = redisCache.getExpire(
                        RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                        TimeUnit.SECONDS
                );
                if (ttlSeconds == null || ttlSeconds <= 0) {
                    ttlSeconds = 3600L;
                }
                redisCache.putHash(statusKey, userIdStr, SubscribeStatus.SUCCESS.getCode(), ttlSeconds, TimeUnit.SECONDS);
                redisCache.expire(statusKey, ttlSeconds, TimeUnit.SECONDS);
                res.add(new GetSubscribeStatusVo(voucherId, SubscribeStatus.SUCCESS.getCode()));
                continue;
            }
            boolean inQueue = Boolean.TRUE.equals(redisCache.isMemberForSet(
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_USER_TAG_KEY, voucherId),
                    userIdStr
            ));
            res.add(new GetSubscribeStatusVo(voucherId, inQueue ? SubscribeStatus.SUBSCRIBED.getCode() : SubscribeStatus.UNSUBSCRIBED.getCode()));
        }
        return res;
    }


    /**
     * Old Version, do not use
     * @param seckillVoucherDto
     * @return
     */
    public Long doAddSeckillVoucherV1(SeckillVoucherDto seckillVoucherDto) {
        VoucherDto voucherDto = new VoucherDto();
        BeanUtil.copyProperties(seckillVoucherDto, voucherDto);
        Long voucherId = addVoucher(voucherDto);
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setId(snowflakeIdGenerator.nextId());
        seckillVoucher.setVoucherId(voucherId);
        seckillVoucher.setStock(seckillVoucherDto.getStock());
        seckillVoucher.setBeginTime(seckillVoucherDto.getBeginTime());
        seckillVoucher.setEndTime(seckillVoucherDto.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucherId, seckillVoucher.getStock().toString());
        long ttlSeconds = Math.max(
                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), seckillVoucher.getEndTime()).getSeconds(),
                1L
        );
        seckillVoucher.setStock(null);
        redisCache.set(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                seckillVoucher,
                ttlSeconds,
                TimeUnit.SECONDS
        );
        return voucherId;
    }
    
    public Long doAddSeckillVoucherV2(SeckillVoucherDto seckillVoucherDto) {

        VoucherDto voucherDto = new VoucherDto();
        BeanUtil.copyProperties(seckillVoucherDto, voucherDto);
        Long voucherId = addVoucher(voucherDto);
        SeckillVoucher seckillVoucher = new SeckillVoucher();

        //Generate seckill voucher Id by snowflakeIdGenerator, and save the seckillVoucher to the database
        seckillVoucher.setId(snowflakeIdGenerator.nextId());
        seckillVoucher.setVoucherId(voucherId);
        seckillVoucher.setInitStock(seckillVoucherDto.getStock());
        seckillVoucher.setStock(seckillVoucherDto.getStock());
        seckillVoucher.setBeginTime(seckillVoucherDto.getBeginTime());
        seckillVoucher.setEndTime(seckillVoucherDto.getEndTime());
        seckillVoucher.setAllowedLevels(seckillVoucherDto.getAllowedLevels());
        seckillVoucher.setMinLevel(seckillVoucherDto.getMinLevel());
        seckillVoucherService.save(seckillVoucher);

        //Set ttl of the voucher
        long ttlSeconds = Math.max(
                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), seckillVoucher.getEndTime()).getSeconds(),
                1L
        );
        //Set the SECKILL_STOCK_TAG_KEY into redis
        redisCache.set(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId),
                String.valueOf(seckillVoucher.getStock()),
                ttlSeconds,
                TimeUnit.SECONDS
        );
        seckillVoucher.setStock(null);

        //Set the SECKILL_VOUCHER_TAG_KEY into redis
        redisCache.set(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_VOUCHER_TAG_KEY, voucherId),
                seckillVoucher,
                ttlSeconds,
                TimeUnit.SECONDS
        );

        sendDelayedVoucherReminder(seckillVoucher);
        return voucherId;
    }
    
    public void sendDelayedVoucherReminder(SeckillVoucher seckillVoucher){


        LocalDateTime beginTime = seckillVoucher.getBeginTime();

        //If beginTime == null, return directly
        if (beginTime == null) {
            log.warn("[DELAY_REMINDER] beginTime is null, skipping scheduling voucherId={}", seckillVoucher.getVoucherId());
            return;
        }


        long secondsUntilBegin = Math.max(
                LocalDateTimeUtil.between(LocalDateTimeUtil.now(), beginTime).getSeconds(),
                0L
        );


        //Set up how much time does the reminder should delay
        long delaySeconds = secondsUntilBegin - Math.max(reminderAheadSeconds, 0L);

        //If delay seconds less than 0, then return directly
        if (delaySeconds <= 0) {
            log.info("[DELAY_REMINDER] beginTime is too close or already started, skipping delayed scheduling voucherId={} beginTime={} delaySeconds={}",
                    seckillVoucher.getVoucherId(), beginTime, delaySeconds);
            return;
        }

        //Create delayed voucher reminder message
        DelayedVoucherReminderMessage msg = new DelayedVoucherReminderMessage(
                seckillVoucher.getVoucherId(),
                beginTime
        );

        //Convert content to JSON format
        String content = JSON.toJSONString(msg);

        //Define Topic
        String topic = SpringUtil.getPrefixDistinctionName() + "-" + DELAY_VOUCHER_REMINDER;

        //Send message to Kafka queue
        delayQueueContext.sendMessage(topic, content, delaySeconds, TimeUnit.SECONDS);
        log.info("[DELAY_REMINDER] Reminder message scheduled voucherId={} delaySeconds={} topic={}", seckillVoucher.getVoucherId(), delaySeconds, topic);
    }
    
    @Override
    public void delayVoucherReminder(DelayVoucherReminderDto delayVoucherReminderDto) {
        SeckillVoucher seckillVoucher = seckillVoucherService.lambdaQuery().eq(SeckillVoucher::getVoucherId, 
                delayVoucherReminderDto.getVoucherId()).one();
        if (Objects.isNull(seckillVoucher)) {
            throw new HmdpFrameException(BaseCode.SECKILL_VOUCHER_NOT_EXIST);
        }
        DelayedVoucherReminderMessage msg = new DelayedVoucherReminderMessage(
                seckillVoucher.getVoucherId(),
                seckillVoucher.getBeginTime()
        );
        String content = JSON.toJSONString(msg);
        String topic = SpringUtil.getPrefixDistinctionName() + "-" + DELAY_VOUCHER_REMINDER;
        Integer delaySeconds = delayVoucherReminderDto.getDelaySeconds();
        delayQueueContext.sendMessage(topic, content, delayVoucherReminderDto.getDelaySeconds(), TimeUnit.SECONDS);
        log.info("[TEST_DELAY_SEND] Reminder message scheduled voucherId={} delaySeconds={} topic={}", seckillVoucher.getVoucherId(), delaySeconds, topic);
    }
}
