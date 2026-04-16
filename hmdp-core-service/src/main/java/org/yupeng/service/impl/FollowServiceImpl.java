package org.yupeng.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.yupeng.dto.Result;
import org.yupeng.dto.UserDTO;
import org.yupeng.entity.Follow;
import org.yupeng.mapper.FollowMapper;
import org.yupeng.service.IFollowService;
import org.yupeng.service.IUserService;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.yupeng.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Follow interface implementation
 * @author: yupeng
 **/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IUserService userService;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        // 1. Get the logged in user
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;
        // 1. Decide whether to follow or unfollow
        if (isFollow) {
            // 2. Follow and add new data
            Follow follow = new Follow();
            follow.setId(snowflakeIdGenerator.nextId());
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSuccess = save(follow);
            if (isSuccess) {
                // Put the id of the following user into the redis set collection sadd userId followerUserId
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        } else {
            // 3. Delete delete from tb_follow where user_id = ? and follow_user_id = ?
            boolean isSuccess = remove(new QueryWrapper<Follow>()
                    .eq("user_id", userId).eq("follow_user_id", followUserId));
            if (isSuccess) {
                // Remove the id of the following user from the Redis collection
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        // 1. Get the logged in user
        Long userId = UserHolder.getUser().getId();
        // 2. Query whether to follow select count(*) from tb_follow where user_id = ? and follow_user_id = ?
        Long count = query().eq("user_id", userId).eq("follow_user_id", followUserId).count();
        // 3. Judgment
        return Result.ok(count > 0);
    }

    @Override
    public Result followCommons(Long id) {
        // 1. Get the current user
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;
        // 2. Find intersection
        String key2 = "follows:" + id;
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if (intersect == null || intersect.isEmpty()) {
            // No intersection
            return Result.ok(Collections.emptyList());
        }
        // 3. Parse the id collection
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        // 4. Query users
        List<UserDTO> users = userService.listByIds(ids)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(users);
    }
}
