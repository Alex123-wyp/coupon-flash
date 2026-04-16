package org.yupeng.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Cache object mapping tool
 * @author: yupeng
 **/
public class CacheUtil {

    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * Build type
     *
     * @param types
     * @return
     */
    public static Type buildType(Type... types) {
        ParameterizedTypeImpl beforeType = null;
        if (types != null && types.length > 0) {
            if (types.length == 1) {
                return new ParameterizedTypeImpl(new Type[]{null}, null,
                        types[0]);
            }

            for (int i = types.length - 1; i > 0; i--) {
                beforeType = new ParameterizedTypeImpl(new Type[]{beforeType == null ? types[i] : beforeType}, null,
                        types[i - 1]);
            }
        }
        return beforeType;
    }

    /**
     * Check if Key is empty or an empty string
     *
     * @param key
     */
    public static void checkNotBlank(String... key) {
        for (String s : key) {
            if (StrUtil.isEmpty(s)) {
                throw new RuntimeException("请求参数缺失");
            }
        }
    }

    /**
     * Check whether the key in redisKeyBuild is empty or an empty string
     *
     * @param redisKeyBuild key wrapper
     */
    public static void checkNotBlank(RedisKeyBuild redisKeyBuild) {
        if (StrUtil.isEmpty(redisKeyBuild.getRelKey())) {
            throw new RuntimeException("请求参数缺失");
        }
    }

    /**
     * Checks whether list is empty or an empty string
     *
     * @param list
     */
    public static void checkNotBlank(Collection<String> list) {
        for (String s : list) {
            if (StrUtil.isEmpty(s)) {
                throw new RuntimeException("请求参数缺失");
            }
        }
    }

    /**
     * Checks whether list is empty or an empty string
     *
     * @param list key collection
     */
    public static void checkNotEmpty(Collection<?> list) {
        for (Object o : list) {
            if (o == null) {
                throw new RuntimeException("请求参数缺失");
            }
        }
    }

    /**
     * Check if object is empty
     *
     * @param object
     */
    public static void checkNotEmpty(Object object) {
        if (isEmpty(object)) {
            throw new RuntimeException("请求参数缺失");
        }
    }
    
    /**
     * Determine whether object is empty
     *
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof String) {
            return StrUtil.isEmpty((String) object);
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        return false;
    }

    public static List<String> getBatchKey(Collection<RedisKeyBuild> list){
        return list.stream().map(RedisKeyBuild::getRelKey).collect(Collectors.toList());
    }

    public static <T> List<T> optimizeRedisList(List<T> list){
        if (Objects.isNull(list)) {
            return new ArrayList<>();
        }
        if (list.size() == 0 || Objects.isNull(list.get(0))) {
            return new ArrayList<>();
        }
        return list;
    }
    
    public static boolean checkRedisListIsEmpty(List<?> list){
        if (Objects.isNull(list)) {
            return true;
        }
        return list.size() == 0 || Objects.isNull(list.get(0));
    }
}
