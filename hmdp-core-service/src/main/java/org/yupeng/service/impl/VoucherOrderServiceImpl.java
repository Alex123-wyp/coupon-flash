package org.yupeng.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataMergedResult;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.core.SpringUtil;
import org.yupeng.dto.CancelVoucherOrderDto;
import org.yupeng.dto.GetVoucherOrderByVoucherIdDto;
import org.yupeng.dto.GetVoucherOrderDto;
import org.yupeng.dto.Result;
import org.yupeng.dto.VoucherReconcileLogDto;
import org.yupeng.entity.SeckillVoucher;
import org.yupeng.entity.UserInfo;
import org.yupeng.entity.Voucher;
import org.yupeng.entity.VoucherOrder;
import org.yupeng.entity.VoucherOrderRouter;
import org.yupeng.enums.BaseCode;
import org.yupeng.enums.BusinessType;
import org.yupeng.enums.LogType;
import org.yupeng.enums.OrderStatus;
import org.yupeng.enums.SeckillVoucherOrderOperate;
import org.yupeng.exception.HmdpFrameException;
import org.yupeng.kafka.message.SeckillVoucherMessage;
import org.yupeng.kafka.producer.SeckillVoucherProducer;
import org.yupeng.kafka.redis.RedisVoucherData;
import org.yupeng.lua.SeckillVoucherDomain;
import org.yupeng.lua.SeckillVoucherOperate;
import org.yupeng.mapper.VoucherOrderMapper;
import org.yupeng.mapper.VoucherOrderRouterMapper;
import org.yupeng.message.MessageExtend;
import org.yupeng.model.SeckillVoucherFullModel;
import org.yupeng.redis.RedisCacheImpl;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.repeatexecutelimit.annotion.RepeatExecuteLimit;
import org.yupeng.service.ISeckillVoucherService;
import org.yupeng.service.IUserInfoService;
import org.yupeng.service.IVoucherOrderRouterService;
import org.yupeng.service.IVoucherOrderService;
import org.yupeng.service.IVoucherReconcileLogService;
import org.yupeng.service.IVoucherService;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.yupeng.utils.RedisIdWorker;
import org.yupeng.utils.UserHolder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.yupeng.constant.Constant.SECKILL_VOUCHER_TOPIC;
import static org.yupeng.constant.RepeatExecuteLimitConstants.SECKILL_VOUCHER_ORDER;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher order interface implementation
 * @author: yupeng
 **/
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    /*
     * Structure guide for this class:
     * Active path: seckillVoucher() -> doSeckillVoucherV2() -> Kafka -> createVoucherOrderV2().
     * Legacy path kept for comparison/reference: doSeckillVoucherV1(), VoucherOrderHandler,
     * handleVoucherOrder(), and createVoucherOrderV1().
     */

    @Resource
    private IVoucherService voucherService;
    
    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    @Resource
    private SeckillVoucherOperate seckillVoucherOperate;
    
    @Resource
    private SeckillVoucherProducer seckillVoucherProducer;
    
    @Resource
    private RedisCacheImpl redisCache;
    
    @Resource
    private IVoucherOrderRouterService voucherOrderRouterService;
    
    @Resource
    private IUserInfoService userInfoService;
    
    @Resource
    private VoucherOrderMapper voucherOrderMapper;
    
    @Resource
    private VoucherOrderRouterMapper voucherOrderRouterMapper;
    
    @Resource
    private RedisVoucherData redisVoucherData;
    
    @Resource
    private IVoucherReconcileLogService voucherReconcileLogService;
    

    // Legacy V1 Lua entry. The current main flow uses SeckillVoucherOperate instead.
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    // Shared background executor. The old stream handler no longer starts here,
    // but this pool is still reused by other async tasks such as auto-issue.
    public static final ThreadPoolExecutor SECKILL_ORDER_EXECUTOR =
            /**
             * corePoolSize: Always keep one worker thread
             * maximumPoolSize: At most one worker thread (So this is effectively a single-thread executor)
             * keepAliveTime: Extra thread will die immediately when idle
             * LinkedBlockingQueue<>(1024): If tasks arrive faster than they can run, they wait in a queue. The queue can hold 1024 tasks at once
             * NamedThreadFactory(""seckill-order-", false): custom thread creator
             * ThreadPoolExecutor.CallerRunsPolicy() - rejection policy
             */
            new ThreadPoolExecutor(
                    1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024),
                    new NamedThreadFactory("seckill-order-", false),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final boolean daemon;
        private final AtomicInteger index = new AtomicInteger(1);

        public NamedThreadFactory(String namePrefix, boolean daemon) {
            this.namePrefix = namePrefix;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + index.getAndIncrement());
            t.setDaemon(daemon);
            t.setUncaughtExceptionHandler((thread, ex) ->
                    log.error("Uncaught exception, thread={}, err={}", thread.getName(), ex.getMessage(), ex)
            );
            return t;
        }
    }
    
    
    @PostConstruct
    private void init(){
        // Legacy Redis Stream bootstrap intentionally disabled.
        // Current async order persistence uses Kafka instead of starting VoucherOrderHandler here.
        //SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    @PreDestroy
    private void destroy(){
        try {
            SECKILL_ORDER_EXECUTOR.shutdown();
            if (!SECKILL_ORDER_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                SECKILL_ORDER_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SECKILL_ORDER_EXECUTOR.shutdownNow();
        }
    }

    // Legacy Redis Stream consumer from the older version.
    // It is kept here for learning/reference, but init() no longer starts it.
    private class VoucherOrderHandler implements Runnable{
        private final String queueName = "stream.orders";
        @Override
        public void run() {
            while (true) {
                try {
                    // 0.Initializestream
                    initStream();
                    // 1. Get the order information in the message queue XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    // 2. Determine whether the order information is empty
                    if (list == null || list.isEmpty()) {
                        // If it is null, it means there is no message and continue with the next loop.
                        continue;
                    }
                    // Parse data
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 3. Create an order
                    handleVoucherOrder(voucherOrder);
                    // 4. Confirm message XACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
                } catch (Exception e) {
                    log.error("Order processing exception", e);
                    handlePendingList();
                }
            }
        }

        public void initStream(){
            Boolean exists = stringRedisTemplate.hasKey(queueName);
            if (BooleanUtil.isFalse(exists)) {
                log.info("Stream does not exist, creating stream");
                // Does not exist, needs to be created
                stringRedisTemplate.opsForStream().createGroup(queueName, ReadOffset.latest(), "g1");
                log.info("Stream and group created");
                return;
            }
            // The stream exists and determines whether the group exists.
            StreamInfo.XInfoGroups groups = stringRedisTemplate.opsForStream().groups(queueName);
            if(groups.isEmpty()){
                log.info("Group does not exist, creating group");
                // The group does not exist, create the group
                stringRedisTemplate.opsForStream().createGroup(queueName, ReadOffset.latest(), "g1");
                log.info("Group created");
            }
        }

        private void handlePendingList() {
            while (true) {
                try {
                    // 1. Get the order information in the message queue XREADGROUP GROUP g1 c1 COUNT 1 STREAMS s1 0
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );
                    // 2. Determine whether the order information is empty
                    if (list == null || list.isEmpty()) {
                        // If it is null, it means there is no message and continue with the next loop.
                        break;
                    }
                    // Parse data
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 3. Create an order
                    handleVoucherOrder(voucherOrder);
                    // 4. Confirm message XACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
                } catch (Exception e) {
                    log.error("Order processing exception", e);
                }
            }
        }
    }


    // Legacy helper used only by the disabled VoucherOrderHandler path.
    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getId();
        // Create lock object
        // SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        // Get lock
        boolean isLock = lock.tryLock();
        // Determine whether the lock acquisition is successful
        if(!isLock){
            // Failed to acquire lock, return error or try again
            log.error("Duplicate orders are not allowed");
            return;
        }
        try {
            // Get the proxy object (transaction)
            createVoucherOrderV1(voucherOrder);
        } finally {
            // release lock
            lock.unlock();
        }
    }

    // Legacy self-proxy used by the V1 flow to trigger transactional methods through Spring AOP.
    IVoucherOrderService proxy;
    /**
     * Grab coupons and place an order
     * */
    @Override
    public Result<Long> seckillVoucher(Long voucherId) {
        // Active entry: V2 is the live path.
        //return doSeckillVoucherV1(voucherId);
        return doSeckillVoucherV2(voucherId);
    }
    
    // Legacy entry kept for comparison with the current Kafka-based implementation.
    // It is no longer selected by seckillVoucher().
    public Result<Long> doSeckillVoucherV1(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        long orderId = snowflakeIdGenerator.nextId();
        // 1. Execute lua script
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)
        );
        int r = result.intValue();
        // 2. Determine whether the result is 0
        if (r != 0) {
            // 2.1. If it is not 0, it means there is no purchase qualification.
            return Result.fail(r == 1 ? "Insufficient stock" : "Duplicate orders are not allowed");
        }
        // 3. Get the proxy object
        proxy = (IVoucherOrderService) AopContext.currentProxy();
        // 4.Return order ID
        return Result.ok(orderId);
    }
    
    public Result<Long> doSeckillVoucherV2(Long voucherId) {
        //Call queryByVoucherId method to find seckillVoucherFullModel in local cache, and then redis
//        SeckillVoucherFullModel seckillVoucherFullModel = seckillVoucherService.queryByVoucherId(voucherId);
        SeckillVoucherFullModel seckillVoucherFullModel = seckillVoucherService.queryByVoucherId(voucherId);
        //Call the method to load stock into redis
        seckillVoucherService.loadVoucherStock(voucherId);
        //Get user ID from threadLocal
//        Long userId = UserHolder.getUser().getId();
        Long userId = UserHolder.getUser().getId();
        //Verify user level
        verifyUserLevel(seckillVoucherFullModel,userId);

        //Generate orderId and traceId by snowflakeIdGenerator
        long orderId = snowflakeIdGenerator.nextId();
        long traceId = snowflakeIdGenerator.nextId();

        /**
         * Create key list: SECKILL_STOCK_TAG_KEY, SECKILL_USER_TAG_KEY, SECKILL_TRACE_LOG_TAG_KEY
         * seckillVoucher.lua side:
         * local stockKey = KEYS[1]
         * local seckillUserKey = KEYS[2]
         * local traceLogKey = KEYS[3]
         */
//        List<String> keys = ListUtil.of(
//                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId).getRelKey(),
//                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId).getRelKey(),
//                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_TRACE_LOG_TAG_KEY, voucherId).getRelKey()
//        );
        List<String> keys = new ArrayList<>(3);
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId).getRelKey());
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId).getRelKey());
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_TRACE_LOG_TAG_KEY, voucherId).getRelKey());

        //Builds 9 lua arguments in args array
        /**
         * Build 9 lua arguments in args array
         * seckillVoucher.lua side:
         * local voucherId = ARGV[1]
         * local userId = ARGV[2]
         * local beginTime = tonumber(ARGV[3])
         * local endTime = tonumber(ARGV[4])
         * local status = tonumber(ARGV[5])
         * local orderId = ARGV[6]
         * local traceId = ARGV[7]
         * local logType = ARGV[8]
         * local ttlSeconds = tonumber(ARGV[9])
         */
//        String[] args = new String[9];
//        args[0] = voucherId.toString();
//        args[1] = userId.toString();
//        args[2] = String.valueOf(LocalDateTimeUtil.toEpochMilli(seckillVoucherFullModel.getBeginTime()));
//        args[3] = String.valueOf(LocalDateTimeUtil.toEpochMilli(seckillVoucherFullModel.getEndTime()));
//        args[4] = String.valueOf(seckillVoucherFullModel.getStatus());
//        args[5] = String.valueOf(orderId);
//        args[6] = String.valueOf(traceId);
//        //Generate traceId - LogType.DEDUCT.getCode() -> -1
//        args[7] = String.valueOf(LogType.DEDUCT.getCode());
//        long secondsUntilEnd = Duration.between(LocalDateTimeUtil.now(), seckillVoucherFullModel.getEndTime()).getSeconds();
//        //The trace log expired day: add one more day from remaining time
//        long ttlSeconds = Math.max(1L, secondsUntilEnd + Duration.ofDays(1).getSeconds());
//        args[8] = String.valueOf(ttlSeconds);
        String[] args = new String[9];
        args[0] = String.valueOf(voucherId);
        args[1] = String.valueOf(userId);
        args[2] = String.valueOf(LocalDateTimeUtil.toEpochMilli(seckillVoucherFullModel.getBeginTime()));
        args[3] = String.valueOf(LocalDateTimeUtil.toEpochMilli(seckillVoucherFullModel.getEndTime()));
        args[4] = String.valueOf(seckillVoucherFullModel.getStatus());
        args[5] = String.valueOf(orderId);
        args[6] = String.valueOf(traceId);
        args[7] = String.valueOf(LogType.DEDUCT.getCode());
        long secondsUntilEnd = Duration.between(LocalDateTimeUtil.now(), seckillVoucherFullModel.getEndTime()).getSeconds();
        long ttlSeconds = Math.max(1L, secondsUntilEnd + Duration.ofDays(1).getSeconds());
        args[8] = String.valueOf(ttlSeconds);
        //pass the keys and args to lua script to execute, return SeckillVoucherDomain class.
        SeckillVoucherDomain seckillVoucherDomain = seckillVoucherOperate.execute(
                keys,
                args
        );

        //If Lua script did not return success, then throws an HmdpFrameException
//        if (!seckillVoucherDomain.getCode().equals(BaseCode.SUCCESS.getCode())) {
//            throw new HmdpFrameException(Objects.requireNonNull(BaseCode.getRc(seckillVoucherDomain.getCode())));
//        }
        BaseCode rc = BaseCode.getRc(seckillVoucherDomain.getCode());
        if(rc == null){
            throw new HmdpFrameException("Unknown seckill result code: " + seckillVoucherDomain.getCode());
        }
        if(!rc.getCode().equals(BaseCode.SUCCESS.getCode()) ){
            throw new HmdpFrameException(rc);
        }
        //If Lua script returns success, then generate a SeckillVoucher message, and send it to kafka message queue
        SeckillVoucherMessage seckillVoucherMessage = new SeckillVoucherMessage(
                userId,
                voucherId,
                orderId,
                traceId,
                seckillVoucherDomain.getBeforeQty(),
                seckillVoucherDomain.getDeductQty(),
                seckillVoucherDomain.getAfterQty(),
                Boolean.FALSE
        );

        //Send message to kafka to ask database to change the value
        seckillVoucherProducer.sendPayload(
                SpringUtil.getPrefixDistinctionName() + "-" + SECKILL_VOUCHER_TOPIC,
                seckillVoucherMessage);

        return Result.ok(orderId);
    }


    public void verifyUserLevel(SeckillVoucherFullModel seckillVoucherFullModel,Long userId){

        //Get allowedLevelsStr from seckillVoucherFullModel stored in redis
        String allowedLevelsStr = seckillVoucherFullModel.getAllowedLevels();
        //Get min level from seckillVoucherFullModel stored in redis
        Integer minLevel = seckillVoucherFullModel.getMinLevel();
        //If allowedLevelsStr is blank or minLevel is null, return directly (In this case treat user can buy the voucher by default)
        boolean hasLevelRule = StrUtil.isNotBlank(allowedLevelsStr) || Objects.nonNull(minLevel);
        if (!hasLevelRule) {
            return;
        }
        //in terms of user, get userInfo first in redis, then in db
        UserInfo userInfo = userInfoService.getByUserId(userId);
        //If userInfo is empty, throw an exception which is USER_NOT_EXIST
        if (Objects.isNull(userInfo)) {
            throw new HmdpFrameException(BaseCode.USER_NOT_EXIST);
        }
        boolean allowed = true;
        Integer level = userInfo.getLevel();
        if (StrUtil.isNotBlank(allowedLevelsStr)) {
            try {
                //This block of code converts array into a set called allowedLevels
                Set<Integer> allowedLevels = Arrays.stream(allowedLevelsStr.split(","))
                        .map(String::trim)
                        .filter(StrUtil::isNotBlank)
                        .map(Integer::valueOf)
                        .collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(allowedLevels)) {
                    allowed = allowedLevels.contains(level);
                }
            } catch (Exception parseEx) {
                log.warn("Failed to parse allowedLevels, voucherId={}, raw={}",
                        seckillVoucherFullModel.getVoucherId(), 
                        allowedLevelsStr, parseEx);
            }
        }
        //If the level of user contained in the level set of voucher, and the level of user >= minLevel of voucher, allow
        if (allowed && Objects.nonNull(minLevel)) {
            allowed = Objects.nonNull(level) && level >= minLevel;
        }

        //If not then throw exception, not allowed
        if (!allowed) {
            throw new HmdpFrameException("The current membership level does not meet the participation requirements");
        }
    }

   
    // Currently unused placeholder for future audience-rule expansion.
    private static class AudienceRule {
        public Set<Integer> allowedLevels;
        public Integer minLevel;
        public Set<String> allowedCities;
        
        boolean hasLevelRule(){
            return (allowedLevels != null && !allowedLevels.isEmpty()) || minLevel != null;
        }
        boolean hasCityRule(){
            return allowedCities != null && !allowedCities.isEmpty();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    // Legacy persistence step for the disabled Redis Stream/V1 path.
    // The current persistence path is createVoucherOrderV2(), called by Kafka consumer.
    public void createVoucherOrderV1(VoucherOrder voucherOrder) {
        // 5. One order per person
        Long userId = voucherOrder.getUserId();

        // 5.1. Query orders
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherOrder.getVoucherId()).count();
        // 5.2. Determine whether it exists
        if (count > 0) {
            // User has already purchased
            log.error("User has already purchased once!");
            return;
        }
        // 6. Deduct inventory
        boolean success = seckillVoucherService.update()
                // set stock = stock - 1
                .setSql("stock = stock - 1")
                // where id = ? and stock > 0
                .eq("voucher_id", voucherOrder.getVoucherId()).gt("stock", 0) 
                .update();
        if (!success) {
            // Deduction failed
            log.error("Insufficient stock!");
            return;
        }
        // 7.Create order
        save(voucherOrder);
    }
    
    
    @Override
    @RepeatExecuteLimit(name = SECKILL_VOUCHER_ORDER,keys = {"#message.uuid"})
    @Transactional(rollbackFor = Exception.class)
    // Active persistence path: Kafka consumer lands here after Redis/Lua reservation succeeds.

    public boolean createVoucherOrderV2(MessageExtend<SeckillVoucherMessage> message) {

        SeckillVoucherMessage messageBody = message.getMessageBody();
        Long userId = messageBody.getUserId();

        /**
         * VoucherOrder table sharding key is userId and voucherId, but sometimes we need to chek the order by orderId,
         * to avoid checking all routes in this case, we need voucher_order_route table
         */
        VoucherOrder normalVoucherOrder = lambdaQuery()
                .eq(VoucherOrder::getVoucherId, messageBody.getVoucherId())
                .eq(VoucherOrder::getUserId, userId)
                .eq(VoucherOrder::getStatus,OrderStatus.NORMAL.getCode())
                .one();
        if (Objects.nonNull(normalVoucherOrder)) {
            log.warn("Order already exists, voucherId={}, userId={}", normalVoucherOrder.getVoucherId(), userId);
            throw new HmdpFrameException(BaseCode.VOUCHER_ORDER_EXIST);
        }
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", messageBody.getVoucherId())
                .gt("stock", 0)
                .update();
        if (!success) {
            throw new HmdpFrameException("Insufficient voucher stock! voucherId=" + messageBody.getVoucherId());
        }
        //Create Order voucher order table
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(messageBody.getOrderId());
        voucherOrder.setUserId(messageBody.getUserId());
        voucherOrder.setVoucherId(messageBody.getVoucherId());
        voucherOrder.setCreateTime(LocalDateTimeUtil.now());
        save(voucherOrder);

        //Create voucher order router table
        VoucherOrderRouter voucherOrderRouter = new VoucherOrderRouter();
        voucherOrderRouter.setId(snowflakeIdGenerator.nextId());
        voucherOrderRouter.setOrderId(voucherOrder.getId());
        voucherOrderRouter.setUserId(userId);
        voucherOrderRouter.setVoucherId(voucherOrder.getVoucherId());
        voucherOrderRouter.setCreateTime(LocalDateTimeUtil.now());
        voucherOrderRouter.setUpdateTime(LocalDateTimeUtil.now());
        voucherOrderRouterService.save(voucherOrderRouter);
        redisCache.set(RedisKeyBuild.createRedisKey(
                RedisKeyManage.DB_SECKILL_ORDER_KEY,messageBody.getOrderId()),
                voucherOrder,
                60, 
                TimeUnit.SECONDS
        );
        voucherReconcileLogService.saveReconcileLog(
                LogType.DEDUCT.getCode(),
                BusinessType.SUCCESS.getCode(),
                "order created",
                message
        );
        return true;
    }
    
    @Override
    public Long getSeckillVoucherOrder(GetVoucherOrderDto getVoucherOrderDto) {
        VoucherOrder voucherOrder = 
                redisCache.get(RedisKeyBuild.createRedisKey(
                        RedisKeyManage.DB_SECKILL_ORDER_KEY, 
                        getVoucherOrderDto.getOrderId()), 
                        VoucherOrder.class);
        if (Objects.nonNull(voucherOrder)) {
            return voucherOrder.getId();
        }
        VoucherOrderRouter voucherOrderRouter = 
                voucherOrderRouterService.lambdaQuery()
                        .eq(VoucherOrderRouter::getOrderId, getVoucherOrderDto.getOrderId())
                        .one();
        if (Objects.nonNull(voucherOrderRouter)) {
            return voucherOrderRouter.getOrderId();
        }
        return null;
    }
    
    @Override
    public Long getSeckillVoucherOrderIdByVoucherId(GetVoucherOrderByVoucherIdDto getVoucherOrderByVoucherIdDto) {
        VoucherOrder voucherOrder = lambdaQuery()
                .eq(VoucherOrder::getUserId, UserHolder.getUser().getId())
                .eq(VoucherOrder::getVoucherId, getVoucherOrderByVoucherIdDto.getVoucherId())
                .eq(VoucherOrder::getStatus, OrderStatus.NORMAL.getCode())
                .one();
        if (Objects.nonNull(voucherOrder)) {
            return voucherOrder.getId();
        }
        return null;
    }

    /**
     * Cancel order
     * @param cancelVoucherOrderDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(CancelVoucherOrderDto cancelVoucherOrderDto) {

        /**
         * Find voucher order
         */
        VoucherOrder voucherOrder = lambdaQuery()
                .eq(VoucherOrder::getUserId, UserHolder.getUser().getId())
                .eq(VoucherOrder::getVoucherId, cancelVoucherOrderDto.getVoucherId())
                .eq(VoucherOrder::getStatus, OrderStatus.NORMAL.getCode())
                .one();
        if (Objects.isNull(voucherOrder)) {
            throw new HmdpFrameException(BaseCode.SECKILL_VOUCHER_ORDER_NOT_EXIST);
        }
        /**
         * Find seckill voucher order
         */
        SeckillVoucher seckillVoucher = seckillVoucherService.lambdaQuery()
                .eq(SeckillVoucher::getVoucherId, cancelVoucherOrderDto.getVoucherId())
                .one();
        if (Objects.isNull(seckillVoucher)) {
            throw new HmdpFrameException(BaseCode.SECKILL_VOUCHER_NOT_EXIST);
        }

        /**
         * Update voucher order table
         */
        boolean updateResult = lambdaUpdate().set(VoucherOrder::getStatus, OrderStatus.CANCEL.getCode())
                .set(VoucherOrder::getUpdateTime, LocalDateTimeUtil.now())
                .eq(VoucherOrder::getUserId, UserHolder.getUser().getId())
                .eq(VoucherOrder::getVoucherId, cancelVoucherOrderDto.getVoucherId())
                .update();
        /**
         * Save to voucher_reconcile_log table
         */
        long traceId = snowflakeIdGenerator.nextId();
        VoucherReconcileLogDto voucherReconcileLogDto = new VoucherReconcileLogDto();
        voucherReconcileLogDto.setOrderId(voucherOrder.getId());
        voucherReconcileLogDto.setUserId(voucherOrder.getUserId());
        voucherReconcileLogDto.setVoucherId(voucherOrder.getVoucherId());
        voucherReconcileLogDto.setDetail("cancel voucher order ");
        voucherReconcileLogDto.setBeforeQty(seckillVoucher.getStock());
        voucherReconcileLogDto.setChangeQty(1);
        voucherReconcileLogDto.setAfterQty(seckillVoucher.getStock() + 1);
        voucherReconcileLogDto.setTraceId(traceId);
        voucherReconcileLogDto.setLogType(LogType.RESTORE.getCode());
        voucherReconcileLogDto.setBusinessType( BusinessType.CANCEL.getCode());
        boolean saveReconcileLogResult = voucherReconcileLogService.saveReconcileLog(voucherReconcileLogDto);

        //seckillVoucher table voucher roll back (We do not save stock in voucher order table, hence no need to roll back in that table)
        boolean rollbackStockResult = seckillVoucherService.rollbackStock(cancelVoucherOrderDto.getVoucherId());

        Boolean result = updateResult && saveReconcileLogResult && rollbackStockResult;

        /**
         * roll back to delete stock from stock key
         */
        if (result) {
            redisVoucherData.rollbackRedisVoucherData(
                    SeckillVoucherOrderOperate.YES,
                    traceId,
                    voucherOrder.getVoucherId(),
                    voucherOrder.getUserId(),
                    voucherOrder.getId(),
                    seckillVoucher.getStock(),
                    1,
                    seckillVoucher.getStock() + 1
            );
            redisCache.delForHash(RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_STATUS_TAG_KEY, 
                    cancelVoucherOrderDto.getVoucherId()),
                    String.valueOf(voucherOrder.getUserId()));

            /**
             * Substract 1 from SECKILL_SHOP_TOP_BUYERS_DAILY_TAG_KEY
             */
            Voucher voucher = voucherService.getById(voucherOrder.getVoucherId());
            if (Objects.nonNull(voucher)) {
                String day = voucherOrder.getCreateTime().format(DateTimeFormatter.BASIC_ISO_DATE);
                RedisKeyBuild dailyKey = RedisKeyBuild.createRedisKey(
                        RedisKeyManage.SECKILL_SHOP_TOP_BUYERS_DAILY_TAG_KEY,
                        voucher.getShopId(),
                        day
                );
                redisCache.incrementScoreForSortedSet(dailyKey, String.valueOf(voucherOrder.getUserId()), -1.0);
            }
            /**
             * Auto issue voucher to the earliest subscriber
             */
            try {
                autoIssueVoucherToEarliestSubscriber(
                        voucherOrder.getVoucherId(), 
                        voucherOrder.getUserId()
                );
            } catch (Exception e) {
                log.warn("Auto-issue failed, voucherId={}, err=\n{}", voucherOrder.getVoucherId(), e.getMessage());
            }
        }
        return result;
    }
    
    @Override
    public boolean autoIssueVoucherToEarliestSubscriber(final Long voucherId, final Long excludeUserId) {

        //Find seckillVoucherFullModel first in redis, and then database
        SeckillVoucherFullModel seckillVoucherFullModel = seckillVoucherService.queryByVoucherId(voucherId);
        if (Objects.isNull(seckillVoucherFullModel) 
                || 
                Objects.isNull(seckillVoucherFullModel.getBeginTime()) 
                ||
                Objects.isNull(seckillVoucherFullModel.getEndTime())) {
            return false;
        }
        //Load stock from the database first (Because we delete redis data by rolling back in previous action)
        seckillVoucherService.loadVoucherStock(voucherId);
        //Find the candidate
        String candidateUserIdStr = findEarliestCandidate(voucherId, excludeUserId);
        if (StrUtil.isBlank(candidateUserIdStr)) {
            return false;
        }
        return issueToCandidate(voucherId, candidateUserIdStr, seckillVoucherFullModel);
    }


    private String findEarliestCandidate(final Long voucherId, final Long excludeUserId) {

        RedisKeyBuild subscribeZSetKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SUBSCRIBE_ZSET_TAG_KEY, voucherId);
        RedisKeyBuild purchasedSetKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId);
        
        final long pageCount = 1L;
        long offset = 0L;
        while (true) {
            //Fetch the user from redis
            Set<ZSetOperations.TypedTuple<String>> page = redisCache.rangeByScoreWithScoreForSortedSet(
                    subscribeZSetKey,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    offset,
                    pageCount,
                    String.class
            );
            //If page does not exist, return
            if (CollectionUtil.isEmpty(page)) {
                return null;
            }
            //Get the current user from the page that has been fetched before, take the next element from the iterator
            ZSetOperations.TypedTuple<String> tuple = page.iterator().next();
            //If that tuple is null, or its value is null, continue
            if (Objects.isNull(tuple) || Objects.isNull(tuple.getValue())) {
                offset++;
                continue;
            }
            //Assign value to uidStr
            String uidStr = tuple.getValue();
            //If uidStr = null, continue
            if (StrUtil.isBlank(uidStr)) {
                offset++;
                continue;
            }
            //if excludedUser Id != null and the current user id = excluded user id, continue search
            if (Objects.nonNull(excludeUserId) && Objects.equals(uidStr, String.valueOf(excludeUserId))) {
                offset++;
                continue;
            }

            //If the current user has already purchased the voucher, then continue search
            Boolean purchased = redisCache.isMemberForSet(purchasedSetKey, uidStr);
            if (BooleanUtil.isTrue(purchased)) {
                offset++;
                continue;
            }

            //Finally return the selected user
            return uidStr;
        }
    }
    
    private boolean issueToCandidate(final Long voucherId, 
                                     final String candidateUserIdStr, 
                                     final SeckillVoucherFullModel seckillVoucherFullModel) {

        Long candidateUserId = Long.valueOf(candidateUserIdStr);
        //We have to verify user level first
        try {
            verifyUserLevel(seckillVoucherFullModel, candidateUserId);
        } catch (Exception e) {
            log.info("Candidate user does not satisfy audience rules, skipping auto-issue. voucherId={}, userId={}", voucherId, candidateUserId);
            return false;
        }

        /**
         * Build keys and args as params transfer to lua script operator
         */
        List<String> keys = buildSeckillKeys(voucherId);
        long orderId = snowflakeIdGenerator.nextId();
        long traceId = snowflakeIdGenerator.nextId();
        String[] args = buildSeckillArgs(voucherId, candidateUserIdStr, seckillVoucherFullModel, orderId, traceId);
        /**
         *Do in lua script: seckillVoucher stock subtraction, add user to set to mark as already purchased, record log
         */
        SeckillVoucherDomain domain = seckillVoucherOperate.execute(keys, args);
        //If not succeed, record in log and return false
        if (!Objects.equals(domain.getCode(), BaseCode.SUCCESS.getCode())) {
            log.info("Auto-issue Lua deduction failed, code={}, voucherId={}, userId={}", domain.getCode(), voucherId, candidateUserId);
            return false;
        }
        /**
         * Build the message that need to send to kafka queue, and notify seller and make database consistent
         */
        SeckillVoucherMessage message = new SeckillVoucherMessage(
                candidateUserId,
                voucherId,
                orderId,
                traceId,
                domain.getBeforeQty(),
                domain.getDeductQty(),
                domain.getAfterQty(),
                Boolean.TRUE
        );
        seckillVoucherProducer.sendPayload(
                SpringUtil.getPrefixDistinctionName() + "-" + SECKILL_VOUCHER_TOPIC,
                message
        );
        return true;
    }
    
    private List<String> buildSeckillKeys(final Long voucherId) {
        String stockKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_STOCK_TAG_KEY, voucherId).getRelKey();
        String userKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_TAG_KEY, voucherId).getRelKey();
        String traceKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_TRACE_LOG_TAG_KEY, voucherId).getRelKey();
        return ListUtil.of(stockKey, userKey, traceKey);
    }
    
    private String[] buildSeckillArgs(final Long voucherId,
                                      final String userIdStr,
                                      final SeckillVoucherFullModel seckillVoucherFullModel,
                                      final long orderId,
                                      final long traceId) {
        String[] args = new String[9];
        args[0] = voucherId.toString();
        args[1] = userIdStr;
        args[2] = String.valueOf(LocalDateTimeUtil.toEpochMilli(seckillVoucherFullModel.getBeginTime()));
        args[3] = String.valueOf(LocalDateTimeUtil.toEpochMilli(seckillVoucherFullModel.getEndTime()));
        args[4] = String.valueOf(seckillVoucherFullModel.getStatus());
        args[5] = String.valueOf(orderId);
        args[6] = String.valueOf(traceId);
        args[7] = String.valueOf(LogType.DEDUCT.getCode());
        args[8] = String.valueOf(computeTtlSeconds(seckillVoucherFullModel));
        return args;
    }
    
    private long computeTtlSeconds(final SeckillVoucherFullModel seckillVoucherFullModel) {
        long secondsUntilEnd = Duration.between(LocalDateTimeUtil.now(), seckillVoucherFullModel.getEndTime()).getSeconds();
        return Math.max(1L, secondsUntilEnd + Duration.ofDays(1).getSeconds());
    }



    /*
     * Historical implementations below are fully disabled and kept only as migration notes
     * from older blocking-queue / direct-lock versions before the current Lua + Kafka flow.
     *
    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);
    private class VoucherOrderHandler implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    // 1. Get order information in the queue
                    VoucherOrder voucherOrder = orderTasks.take();
                    // 2. Create an order
                    handleVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("Order processing exception", e);
                }
            }
        }
    }

    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        // 1. Execute lua script
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString()
        );
        int r = result.intValue();
        // 2. Determine whether the result is 0
        if (r != 0) {
            // 2.1. If it is not 0, it means there is no purchase qualification.
            return Result.fail(r == 1 ? "Insufficient stock" : "Duplicate orders are not allowed");
        }
        // 2.2. If it is 0, you are eligible to purchase and save the order information to the blocking queue.
        VoucherOrder voucherOrder = new VoucherOrder();
        // 2.3.order ID
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 2.4.user ID
        voucherOrder.setUserId(userId);
        // 2.5. Voucher ID
        voucherOrder.setVoucherId(voucherId);
        // 2.6. Put into blocking queue
        orderTasks.add(voucherOrder);
        // 3. Get the proxy object
        proxy = (IVoucherOrderService) AopContext.currentProxy()
        // 4.Return order ID
        return Result.ok(orderId);
    }*/
    /*@Override
    public Result seckillVoucher(Long voucherId) {
        // 1. Check coupons
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // 2. Determine whether the flash sale has started
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // Not started yet
            return Result.fail("Seckill has not started yet!");
        }
        // 3. Determine whether the flash sale has ended
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // Not started yet
            return Result.fail("Seckill has ended!");
        }
        // 4. Determine whether the inventory is sufficient
        if (voucher.getStock() < 1) {
            // Insufficient stock
            return Result.fail("Insufficient stock!");
        }

        Long userId = UserHolder.getUser().getId();
        // Create lock object
        // SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        // Get lock
        boolean isLock = lock.tryLock();
        // Determine whether the lock acquisition is successful
        if(!isLock){
            // Failed to acquire lock, return error or try again
            return Result.fail("Duplicate orders are not allowed");
        }
        try {
            // Get the proxy object (transaction)
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        } finally {
            // release lock
            lock.unlock();
        }
    }*/


    /*@Transactional
    public Result createVoucherOrder(Long voucherId) {
        // 5. One order per person
        Long userId = UserHolder.getUser().getId();

        synchronized (userId.toString().intern()) {
            // 5.1. Query orders
            int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
            // 5.2. Determine whether it exists
            if (count > 0) {
                // User has already purchased
                return Result.fail("User has already purchased once!");
            }

            // 6. Deduct inventory
            boolean success = seckillVoucherService.update()
                    .setSql("stock = stock - 1") // set stock = stock - 1
                    .eq("voucher_id", voucherId).gt("stock", 0) // where id = ? and stock > 0
                    .update();
            if (!success) {
                // Deduction failed
                return Result.fail("Insufficient stock!");
            }

            // 7.Create order
            VoucherOrder voucherOrder = new VoucherOrder();
            // 7.1.order ID
            long orderId = redisIdWorker.nextId("order");
            voucherOrder.setId(orderId);
            // 7.2.user ID
            voucherOrder.setUserId(userId);
            // 7.3. Voucher id
            voucherOrder.setVoucherId(voucherId);
            save(voucherOrder);

            // 7.Return order ID
            return Result.ok(orderId);
        }
    }*/

}
