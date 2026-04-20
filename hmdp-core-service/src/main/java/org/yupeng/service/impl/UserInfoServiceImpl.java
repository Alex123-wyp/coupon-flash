package org.yupeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.dto.Result;
import org.yupeng.entity.UserInfo;
import org.yupeng.enums.BaseCode;
import org.yupeng.exception.HmdpFrameException;
import org.yupeng.mapper.UserInfoMapper;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.IUserInfoService;
import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.annotion.ServiceLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.yupeng.constant.DistributedLockConstants.UPDATE_USER_INFO_LOCK;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User information interface implementation
 * @author: yupeng
 **/
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    private RedisCache redisCache;
    
    @Override
    @ServiceLock(lockType= LockType.Read,name = UPDATE_USER_INFO_LOCK,keys = {"#userId"})
    public UserInfo getByUserId(Long userId){
        UserInfo userInfo = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_INFO_KEY, userId), UserInfo.class);
        if (Objects.nonNull(userInfo)){
            return userInfo;
        }
        userInfo = lambdaQuery().eq(UserInfo::getUserId, userId).one();
        if (Objects.isNull(userInfo)) {
            throw new HmdpFrameException(BaseCode.USER_NOT_EXIST);
        }
        redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_INFO_KEY, userId), userInfo);
        return userInfo;
    }
    
    @Override
    @ServiceLock(lockType= LockType.Write,name = UPDATE_USER_INFO_LOCK,keys = {"#userId"})
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateUserLevel(Long userId, Integer newLevel) {
        if (Objects.isNull(userId) || Objects.isNull(newLevel) || newLevel <= 0) {
            return Result.fail("Invalid parameters: userId/newLevel");
        }
        UserInfo userInfo = this.lambdaQuery()
                .eq(UserInfo::getUserId, userId)
                .one();
        if (Objects.isNull(userInfo)) {
            return Result.fail("User information does not exist");
        }
        Integer oldLevel = userInfo.getLevel();
        if (Objects.equals(oldLevel, newLevel)) {
            return Result.ok();
        }
        // Update database level
        boolean updated = this.lambdaUpdate()
                .set(UserInfo::getLevel, newLevel)
                .eq(UserInfo::getUserId, userId)
                .update();
        if (!updated) {
            return Result.fail("Failed to update level");
        }
        // Delete user information cache
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_INFO_KEY, userId));
        // Maintain Redis set inverted indexes (best-effort and does not affect transaction commits)
        try {
            if (Objects.nonNull(oldLevel) && oldLevel > 0) {
                redisCache.removeForSet(
                        RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_LEVEL_MEMBERS_TAG_KEY, oldLevel),
                        userId
                );
            }
            redisCache.addForSet(
                    RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_LEVEL_MEMBERS_TAG_KEY, newLevel),
                    userId
            );
        } catch (Exception e) {
            // Log the event without rolling back the business transaction
            log.error("Failed to maintain user level sets userId={} oldLevel={} newLevel={}", userId, oldLevel, newLevel, e);
        }
        return Result.ok();
    }

}
