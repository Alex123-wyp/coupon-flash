package org.yupeng.handler;


import org.yupeng.core.SpringUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Single Bloom filter wrapper
 * @author: yupeng
 **/
public class BloomFilterHandler {

    private final RBloomFilter<String> bloomFilter;

    public BloomFilterHandler(RedissonClient redissonClient, 
                              String name, 
                              Long expectedInsertions, 
                              Double falseProbability){
        RBloomFilter<String> bf = redissonClient.getBloomFilter(
                SpringUtil.getPrefixDistinctionName() 
                        + "-" 
                        + name);
        bf.tryInit(expectedInsertions == null ? 
                        20000L : expectedInsertions,
                falseProbability == null ? 
                        0.01D : falseProbability);
        this.bloomFilter = bf;
    }

    public boolean add(String data) {
        return bloomFilter.add(data);
    }

    public boolean contains(String data) {
        return bloomFilter.contains(data);
    }
}