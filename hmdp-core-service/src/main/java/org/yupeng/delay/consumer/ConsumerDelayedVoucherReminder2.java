package org.yupeng.delay.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yupeng.core.ConsumerTask;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.core.SpringUtil;
import org.yupeng.delay.message.DelayedVoucherReminderMessage;
import org.yupeng.entity.UserInfo;
import org.yupeng.model.SeckillVoucherFullModel;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.ISeckillVoucherService;
import org.yupeng.service.IUserInfoService;

import javax.swing.text.DateFormatter;
import java.net.Inet4Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.yupeng.constant.Constant.DELAY_VOUCHER_REMINDER;

@Slf4j
public class ConsumerDelayedVoucherReminder2 implements ConsumerTask {

    @Resource
    private RedisCache redisCache;

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private IUserInfoService userInfoService;

    @Value("${seckill.reminder.notify.default.minLevel:1}")
    private int defaultMinLevel;
    /**
     * Maximum number of users notified per reminder to avoid notifying too many users at once
     * */
    @Value("${seckill.reminder.notify.max.users:1000}")
    private int maxNotifyUsers;

    @Value("${seckill.reminder.notify.user.level.max:10}")
    private int maxUserLevel;

    @Value("${seckill.reminder.notify.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${seckill.reminder.notify.app.enabled:false}")
    private boolean appEnabled;

    @Value("${seckill.reminder.notify.sms.to:}")
    private String smsTo;

    @Value("${seckill.reminder.dedup.window.seconds:1800}")
    private long dedupWindowSeconds;

    @Value("${seckill.reminder.notify.top.buyers.enabled:true}")
    private boolean topBuyersEnabled;

    @Value("${seckill.reminder.notify.top.buyers.count:200}")
    private int topBuyersCount;

    @Value("${seckill.reminder.notify.top.buyers.days:30}")
    private int topBuyersDays;


    /**
     * Find some valid users to notify
     * Get the seckillVoucherFullModel -> Find the allowedLevel and minLevel
     * @param content
     */
    @Override
    public void execute(String content) {
        try{
            DelayedVoucherReminderMessage msg = parseMessage(content);
            if(msg == null){
                return;
            }
            Long voucherId = msg.getVoucherId();
            SeckillVoucherFullModel seckillVoucherFullModel = seckillVoucherService.queryByVoucherId(voucherId);
            if(seckillVoucherFullModel == null || seckillVoucherFullModel.getBeginTime() == null){
                log.warn("[DELAYED_REMINDER_CONSUMER] Can not find seckillVoucherFullModel={}", seckillVoucherFullModel);
                return;
            }
            LocalDateTime beginTime = seckillVoucherFullModel.getBeginTime();
            if(Objects.isNull(seckillVoucherFullModel)){
                log.warn("[DELAYED_REMINDER_CONSUMER] Voucher={} does not exist", seckillVoucherFullModel);
                return;
            }
            String allowedLevel = seckillVoucherFullModel.getAllowedLevels();
            Integer minLevel = seckillVoucherFullModel.getMinLevel();
            //Get the final valid users
            Set<String> finalValidUsers = buildAudienceUserIds(seckillVoucherFullModel);
            int notificationCount = notifyUsers(voucherId, beginTime, finalValidUsers);
            log.info("[DELAY_REMINDER_CONSUMER] Reminder completed voucherId={} totalUsers={} notified={}",
                    voucherId, finalValidUsers.size(), notificationCount);
        }catch (Exception e){
            log.warn("[DELAY_REMINDER_CONSUMER] Execution exception", e);
        }
    }


    private DelayedVoucherReminderMessage parseMessage(String content){
        try{
            DelayedVoucherReminderMessage msg = new DelayedVoucherReminderMessage();
            msg = JSON.parseObject(content, msg.getClass());
            if(msg == null || msg.getVoucherId() == null){
                log.warn("[DELAYED_REMINDER_CONSUMER] Failed to parse the content={}", content );
                return null;
            }
            return msg;

        }catch (Exception e) {
            log.warn("[DELAYED_REMINDER_CONSUMER] Failed to deserialized content={}", content);
            return null;
        }
    }

    private Set<String> buildAudienceUserIds (SeckillVoucherFullModel voucherFullModel){
        if(Objects.isNull(voucherFullModel)){
            log.warn("[DELAYED_REMINDER_CONSUMER] Failed to load voucherFullModel={}", voucherFullModel);
            return Collections.emptySet();
        }

        String allowedLevel = voucherFullModel.getAllowedLevels();
        Integer minLevel = voucherFullModel.getMinLevel();

        Long shopId = voucherFullModel.getShopId();
        List<UserInfo> userInfoList = queryValidUserSet(allowedLevel, minLevel, maxNotifyUsers);
        Set<String> userIdSet = toUserIdSet(userInfoList);
        //Get top buyer if topBuyersEnabled
        if(topBuyersEnabled){
            List<Long> topBuyers = readTopBuyersfromRedis(shopId, topBuyersCount, topBuyersDays);
            if(CollectionUtil.isNotEmpty(topBuyers)){
                for(Long topBuyer : topBuyers){
                    if(topBuyer == null){
                        continue;
                    }
                    userIdSet.add(String.valueOf(topBuyer));
                }
                return userIdSet;
            }
        }else{
            log.warn("[DELAYED_REMINDER_CONSUMER] skipping to statistic top buyer");
        }
        return userIdSet;
    }

    /**
     * Query valid user from redis or database
     * @param allowedLevel
     * @param minLevel
     * @param maxNotification
     * @return
     */
    private List<UserInfo> queryValidUserSet(String allowedLevel, Integer minLevel, int maxNotification) {

        //If the voucher has allowed users:
        if(StrUtil.isNotBlank(allowedLevel)){
            List<Integer> levelList = parseAllowedLevelToList(allowedLevel);

            //If level list is not null or empty
            if(CollectionUtil.isNotEmpty(levelList)){
                List<Long> fromRedis = readUserIdFromLevelSet(levelList, maxNotification);
                if(CollectionUtil.isNotEmpty(fromRedis)){
                    List<UserInfo> userInfoList = new ArrayList<>(fromRedis.size());
                    for(Long uid : fromRedis){
                        if(uid != null){
                            UserInfo userInfo = new UserInfo();
                            userInfo.setUserId(uid);
                            userInfoList.add(userInfo);
                        }
                    }
                    return userInfoList;
                }
                //If redis is null, check the database
                Set<Integer> levelSet = new HashSet<>(levelList);
                return userInfoService.lambdaQuery()
                                .select(UserInfo::getUserId, UserInfo::getLevel)
                                .in(UserInfo::getLevel, levelSet)
                                .last("limit " + maxNotification)
                                .list();
            }

            //If the allowedLevel is empty: we use the levels between minLevel and maxLevel instead
            int min = Objects.nonNull(minLevel) ? minLevel : defaultMinLevel;

            List<Long> fromRedis = readUserIdFromLevelSet(buildLevelRange(min, Math.max(maxUserLevel, min)), maxNotification);
            if(CollectionUtil.isNotEmpty(fromRedis)){
                List<UserInfo> userInfoList = new ArrayList<>(fromRedis.size());
                for(Long uid : fromRedis){
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(uid);
                    userInfoList.add(userInfo);
                }
                return userInfoList;
            }
            Set<Integer> levelSet = new HashSet<>(buildLevelRange(min, Math.max(maxUserLevel, min)));
            return userInfoService.lambdaQuery()
                    .select(UserInfo::getUserId, UserInfo::getLevel)
                    .in(UserInfo::getLevel, levelSet)
                    .last("limit " + maxNotification)
                    .list();
        }
        //Parse the allowedLevel from String to List<Integer>

        if(Objects.nonNull(minLevel)){
            List<Integer> levelRange = buildLevelRange(minLevel, maxUserLevel);
            List<Long> fromRedis = readUserIdFromLevelSet(levelRange, maxNotification);
            if(CollectionUtil.isNotEmpty(fromRedis)){
                List<UserInfo> userInfos = new ArrayList<>(fromRedis.size());
                for(Long uid : fromRedis){
                    if(uid == null){
                        continue;
                    }
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(uid);
                    userInfos.add(userInfo);
                }
                return userInfos;
            }
            //Find from the database
            Set<Integer> levelSet = new HashSet<>(buildLevelRange(minLevel, maxUserLevel));
            return userInfoService.lambdaQuery()
                    .select(UserInfo::getUserId, UserInfo::getLevel)
                    .in(UserInfo::getLevel, levelSet)
                    .last("limit " + maxNotification)
                    .list();
        }
        minLevel = defaultMinLevel;
        List<Integer> levelRange = buildLevelRange(minLevel, maxUserLevel);
        List<Long> fromRedis = readUserIdFromLevelSet(levelRange, maxNotification);
        if(CollectionUtil.isNotEmpty(fromRedis)){
            List<UserInfo> userInfos = new ArrayList<>(fromRedis.size());
            for(Long uid : fromRedis){
                if(uid == null){
                    continue;
                }
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(uid);
                userInfos.add(userInfo);
            }
            return userInfos;
        }
        //Find from the database
        Set<Integer> levelSet = new HashSet<>(buildLevelRange(minLevel, maxUserLevel));
        return userInfoService.lambdaQuery()
                .select(UserInfo::getUserId, UserInfo::getLevel)
                .in(UserInfo::getLevel, levelSet)
                .last("limit " + maxNotification)
                .list();

    }

    private List<Long> readUserIdFromLevelSet(List<Integer> levelList, int count){

        if(CollectionUtil.isEmpty(levelList)){
            return Collections.emptyList();
        }
        //Find the corresponding user level in redis
        //Create key list
        List<RedisKeyBuild> keys = new ArrayList<>(levelList.size());
        for(Integer lv : levelList){
            if(lv == null){
                continue;
            }
            RedisKeyBuild key = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_LEVEL_MEMBERS_TAG_KEY, lv);
            keys.add(key);
        }
        if(keys.isEmpty()){
            return Collections.emptyList();
        }
        //If there is only one key int the list
        if(keys.size() == 1){
            RedisKeyBuild onlyKey = keys.get(0);
            Set<Long> r = redisCache.distinctRandomMembersForSet(onlyKey, Math.max(1, count), Long.class);
            return new ArrayList<>(r);
        }
        //if there are 2 or more than 2 keys in the list
        if(keys.size() >= 2){
            //Take the union first
            String label = levelList.get(0) + "-" + levelList.get(levelList.size() - 1);
            RedisKeyBuild unionSetKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_LEVEL_MEMBERS_UNION_TAG_KEY, label);
            RedisKeyBuild baseKey =  keys.get(0);
            Collection<RedisKeyBuild> others = keys.subList(1, keys.size());
            try{
                redisCache.unionAndStoreForSet(baseKey, others, unionSetKey);
                redisCache.expire(unionSetKey, 60, TimeUnit.SECONDS);
            }catch (Exception e){
                log.warn("[DELAY_REMINDER_CONSUMER] SET union failed levels={} label={}", levelList, label, e);
            }
            Set<Long> r = redisCache.distinctRandomMembersForSet(unionSetKey, Math.max(1, count), Long.class);
            return new ArrayList<>(r);
        }
        else{
            return Collections.emptyList();
        }
    }

    private List<Integer> parseAllowedLevelToList(String allowedLevel){
        if(allowedLevel == null){
            log.warn("[DELAYED_REMINDER_CONSUMER] Failed to Load allowed level={}", allowedLevel);
            return null;
        }
        String[] allowedLevelStrings = allowedLevel.split(",");
        List<Integer> allowedLevelList = new ArrayList<>(allowedLevelStrings.length);
        for(int i = 0; i < allowedLevelStrings.length; i++){
            if(allowedLevelStrings[i] == null){
                continue;
            }
            Integer level = Integer.valueOf(allowedLevelStrings[i].trim());
            allowedLevelList.add(level);
        }
        return allowedLevelList;
    }

    private List<Integer> buildLevelRange(int min, int max){
        List<Integer> levelRange = new ArrayList<>();
        int size = max - min + 1;
        for(int i = min; i <= max; i++){
            levelRange.add(i);
        }
        return levelRange;
    }

    private Set<String> toUserIdSet(List<UserInfo> userInfoList) {

        Set<String> userIds = new LinkedHashSet<>(userInfoList.size());

        if (CollectionUtil.isEmpty(userInfoList)) {
            return Collections.emptySet();
        }
        for(UserInfo userinfo : userInfoList){
            if(userinfo != null && userinfo.getUserId() != null){
                String userId = String.valueOf(userinfo.getUserId());
                userIds.add(userId);
            }
        }
        return userIds;
    }

    //Read from tag SECKILL_SHOP_TOP_BUYERS_DAILY_TAG_KEY
    private List<Long> readTopBuyersfromRedis(Long shopId, int count, int days) {
        try {
            if(shopId == null){
                return Collections.emptyList();
            }
            List<RedisKeyBuild> keys = new ArrayList<>(days);
            LocalDate today = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
            for (int i = 0; i < Math.max(1, days); i++) {
                String day = today.minusDays(i).format(dtf);
                RedisKeyBuild key = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SHOP_TOP_BUYERS_DAILY_TAG_KEY, shopId, day);
                keys.add(key);
            }
            //If keys list is empty
            if (CollectionUtil.isEmpty(keys)) {
                return Collections.emptyList();
            }
            //Find users in set
            if (keys.size() == 1) {
                RedisKeyBuild onlyKey = keys.get(0);
                Set<Long> selectedSortedSet = redisCache.getReverseRangeForSortedSet(keys.get(0), 0, Math.max(0, count - 1), Long.class);
                return new ArrayList<>(selectedSortedSet);
            }
            String rangeLabel = today.minusDays(keys.size() - 1).format(dtf) + "-" + today.format(dtf);
            RedisKeyBuild unionKey = RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_SHOP_TOP_BUYERS_UNION_TAG_KEY, shopId, rangeLabel);
            RedisKeyBuild baseKey = keys.get(0);
            List<RedisKeyBuild> others = keys.subList(1, keys.size());
            try {
                redisCache.unionAndStoreForSortedSet(baseKey, others, unionKey);
                redisCache.expire(unionKey, 60L, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("[DELAYED_REMINDER_CONSUMER] Failed to Load unionKey={}", unionKey);
            }
            Set<Long> selectedSortedSet = redisCache.getReverseRangeForSortedSet(unionKey, 0, Math.max(0, count - 1), Long.class);
            return new ArrayList<>(selectedSortedSet);

        } catch (Exception e) {
            log.warn("[DELAYED_REMINDER_CONSUMER] Failed to Parse Top Buyers from Redis shopId={}, count={}, days={}", shopId, count, days);
            return Collections.emptyList();
        }
    }

    /**
     * Notification logic
     * @param voucherId
     * @param beginTime
     * @param useIdStrs
     * @return
     */
    private int notifyUsers(Long voucherId, LocalDateTime beginTime, Set<String> useIdStrs){
            int notifyUserCount = 0;
            for(String userIdStr : useIdStrs){
                String notifyContent = String.format("[REMINDER] voucherId=%s userId=%s beginTime=%s",
                        voucherId, userIdStr, beginTime);

                if (StringUtils.isBlank(userIdStr)){
                    continue;
                }
                try{
                    boolean shouldNotify = redisCache.setIfAbsent(
                            RedisKeyBuild.createRedisKey(
                                    RedisKeyManage.SECKILL_REMINDER_NOTIFY_DEDUP_KEY,
                                    voucherId, userIdStr),
                            "1",
                            dedupWindowSeconds,
                            TimeUnit.SECONDS);
                    if (!shouldNotify) {
                        continue;
                    }
                }catch (Exception e){
                    log.warn("[DELAYED_REMINDER_CONSUMER] Failed to find the seller should be notified");
                }
                if(smsEnabled && !smsTo.isEmpty()) {
                    log.info("[REMINDER_SMS] to={} content={}", smsTo, notifyContent);
                    notifyUserCount++;
                }
                if (appEnabled) {
                log.info("[REMINDER_APP] userId={} content={}", userIdStr, notifyContent);
                    notifyUserCount++;
                }


            }
            return notifyUserCount;

    }





    @Override
    public String topic() {
        return SpringUtil.getPrefixDistinctionName() + "-" + DELAY_VOUCHER_REMINDER;
    }
}
