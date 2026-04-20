package org.yupeng.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.dto.LoginFormDTO;
import org.yupeng.dto.Result;
import org.yupeng.dto.UserDTO;
import org.yupeng.entity.User;
import org.yupeng.entity.UserInfo;
import org.yupeng.entity.UserPhone;
import org.yupeng.mapper.UserMapper;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.IUserInfoService;
import org.yupeng.service.IUserPhoneService;
import org.yupeng.service.IUserService;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.yupeng.utils.RegexUtils;
import org.yupeng.utils.UserHolder;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.yupeng.utils.RedisConstants.LOGIN_CODE_KEY;
import static org.yupeng.utils.RedisConstants.LOGIN_CODE_TTL;
import static org.yupeng.utils.RedisConstants.LOGIN_USER_KEY;
import static org.yupeng.utils.RedisConstants.LOGIN_USER_TTL;
import static org.yupeng.utils.RedisConstants.USER_SIGN_KEY;
import static org.yupeng.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User interface implementation
 * @author: yupeng
 **/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    @Resource
    private IUserInfoService userInfoService;
    
    @Resource
    private IUserPhoneService userPhoneService;

    @Resource
    private RedisCache redisCache;

    @Override
    public Result<String> sendCode(String phone, HttpSession session) {
        // 1. Verify mobile phone number
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. If it does not match, return an error message
            return Result.fail("Invalid phone number format!");
        }
        // 3. Comply and generate verification code
        String code = RandomUtil.randomNumbers(6);

        // 4. Save the verification code to session
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 5.Send verification code
        log.info("SMS verification code sent successfully, code: {}", code);
        // Return OK
        return Result.ok(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> login(LoginFormDTO loginForm, HttpSession session) {
        // 1. Verify mobile phone number
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. If it does not match, return an error message
            return Result.fail("Invalid phone number format!");
        }
        // 3. Get the verification code from redis and verify it
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            // Inconsistent, error reported
            return Result.fail("Incorrect verification code");
        }

        // 4. Query users based on mobile phone number
        UserPhone userPhone = userPhoneService.lambdaQuery().eq(UserPhone::getPhone, phone).one();

        User user = null;

        // 5. Determine whether the user exists
        if (userPhone == null) {
            // 6. Does not exist, create a new user and save it
            user = createUserWithPhone(phone);
        }else {
            user = lambdaQuery().eq(User::getPhone, userPhone.getPhone()).one();
        }

        // 7. Save user information to redis
        // 7.1. Randomly generate token as login token
        String token = UUID.randomUUID().toString(true);
        // 7.2. Convert User object to HashMap storage
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        //Convert hash value to string, redis hash value is expected to string
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 7.3.Storage
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4. Set the token validity period (set in seconds to avoid Redisson pExpire recursion problems)
        stringRedisTemplate.expire(
                tokenKey,
                TimeUnit.SECONDS.convert(LOGIN_USER_TTL, TimeUnit.MINUTES),
                TimeUnit.SECONDS
        );

        // 8.Return token
        try {
            maintainLevelSetMembership(user.getId());
        } catch (Exception e) {
            // Ignore exceptions to avoid affecting login
        }
        return Result.ok(token);
    }

    @Override
    public Result<Void> sign() {
        // 1. Get the current logged in user
        Long userId = UserHolder.getUser().getId();
        // 2. Get the date
        LocalDateTime now = LocalDateTime.now();
        // 3. Splice key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4. Get the day of the month today is
        int dayOfMonth = now.getDayOfMonth();
        // 5. Write Redis SETBIT key offset 1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        return Result.ok();
    }

    @Override
    public Result<Integer> signCount() {
        // 1. Get the current logged in user
        Long userId = UserHolder.getUser().getId();
        // 2. Get the date
        LocalDateTime now = LocalDateTime.now();
        // 3. Splice key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4. Get the day of the month today is
        int dayOfMonth = now.getDayOfMonth();
        // 5. Get all the sign-in records of this month as of today, and return a decimal number BITFIELD sign:5:202203 GET u14 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (result == null || result.isEmpty()) {
            // No sign-in results
            return Result.ok(0);
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return Result.ok(0);
        }
        // 6. Loop through
        int count = 0;
        while (true) {
            // 6.1. Let this number be ANDed with 1 to get the last bit of the number // Determine whether this bit is 0
            if ((num & 1) == 0) {
                // If it is 0, it means no sign-in, end
                break;
            }else {
                // If it is not 0, it means you have signed in and the counter +1
                count++;
            }
            // Shift the number one bit to the right, discard the last bit, and continue with the next bit.
            num >>>= 1;
        }
        return Result.ok(count);
    }
    
    private User createUserWithPhone(String phone) {
        // 1.Create user
        User user = new User();
        user.setId(snowflakeIdGenerator.nextId());
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 2.Save user
        save(user);
        // 3. Save user information
        UserInfo userInfo = new UserInfo();
        userInfo.setId(snowflakeIdGenerator.nextId());
        userInfo.setUserId(user.getId());
        userInfo.setLevel(1);
        userInfoService.save(userInfo);
        try {
            maintainLevelSetMembership(user.getId());
        } catch (Exception e) {
            // Ignore exceptions to avoid affecting registration logic
        }
        // 4. Save user mobile phone information
        UserPhone userPhone = new UserPhone();
        userPhone.setId(snowflakeIdGenerator.nextId());
        userPhone.setUserId(user.getId());
        userPhone.setPhone(phone);
        userPhoneService.save(userPhone);
        return user;
    }

    //Assign user to redis set group by user level
    private void maintainLevelSetMembership(Long userId) {
        if (userId == null) {
            return;
        }
        UserInfo info = userInfoService.lambdaQuery().eq(UserInfo::getUserId, userId).one();
        if (info == null || info.getLevel() == null || info.getLevel() <= 0) {
            return;
        }
        Integer level = info.getLevel();
        redisCache.addForSet(
                RedisKeyBuild.createRedisKey(RedisKeyManage.SECKILL_USER_LEVEL_MEMBERS_TAG_KEY, level),
                userId
        );
    }
}
