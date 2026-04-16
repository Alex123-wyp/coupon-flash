package org.yupeng.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Redis data
 * @author: yupeng
 **/
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
