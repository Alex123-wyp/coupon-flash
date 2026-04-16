package org.yupeng.toolkit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Snowflake algorithm constants
 * @author: yupeng
 **/
public class IdGeneratorConstant {
    /**
     * Machine ID bit length
     */
    public static final long WORKER_ID_BITS = 5L;
    public static final long DATA_CENTER_ID_BITS = 5L;
    public static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    public static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
}
