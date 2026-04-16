package org.yupeng.redis;


import org.yupeng.core.RedisKeyManage;
import org.yupeng.core.SpringUtil;
import lombok.Getter;

import java.util.Objects;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Redis key wrapper
 * @author: yupeng
 **/
@Getter
public final class RedisKeyBuild {
    /**
     * actual key used
     * */
    private final String relKey;

    private RedisKeyBuild(String relKey) {
        this.relKey = relKey;
    }

    /**
     * Build real key
     * @param redisKeyManage key enumeration
     * @param args value of placeholder
     * */
    public static RedisKeyBuild createRedisKey(RedisKeyManage redisKeyManage, Object... args){
        String redisRelKey = String.format(redisKeyManage.getKey(),args);
        return new RedisKeyBuild(SpringUtil.getPrefixDistinctionName() + "-" + redisRelKey);
    }
    
    public static String getRedisKey(RedisKeyManage redisKeyManage) {
        return SpringUtil.getPrefixDistinctionName() + "-" + redisKeyManage.getKey();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedisKeyBuild that = (RedisKeyBuild) o;
        return relKey.equals(that.relKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relKey);
    }
}
