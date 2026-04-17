package org.yupeng.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.cache.ShopLocalCache;
import org.yupeng.core.RedisKeyManage;
import org.yupeng.dto.Result;
import org.yupeng.entity.Shop;
import org.yupeng.handler.BloomFilterHandlerFactory;
import org.yupeng.mapper.ShopMapper;
import org.yupeng.redis.RedisCache;
import org.yupeng.redis.RedisKeyBuild;
import org.yupeng.service.IShopService;
import org.yupeng.servicelock.LockType;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.yupeng.util.ServiceLockTool;
import org.yupeng.utils.CacheClient;
import org.yupeng.utils.SystemConstants;
import org.redisson.api.RLock;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.yupeng.constant.Constant.BLOOM_FILTER_HANDLER_SHOP;
import static org.yupeng.utils.RedisConstants.CACHE_SHOP_KEY;
import static org.yupeng.utils.RedisConstants.CACHE_SHOP_TTL;
import static org.yupeng.utils.RedisConstants.LOCK_SHOP_KEY;
import static org.yupeng.utils.RedisConstants.SHOP_GEO_KEY;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Shop interface implementation
 * @author: yupeng
 **/
@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;
    
    @Resource
    private ServiceLockTool serviceLockTool;
    
    @Resource
    private RedisCache redisCache;
    
    @Resource
    private BloomFilterHandlerFactory bloomFilterHandlerFactory;
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Resource
    private ShopLocalCache shopLocalCache;
    
    
    @Override
    public Result<Long> saveShop(final Shop shop) {
        // Write to database
        shop.setId(snowflakeIdGenerator.nextId());
        save(shop);
        // Write Bloom filter (store business)
        bloomFilterHandlerFactory.get(BLOOM_FILTER_HANDLER_SHOP).add(String.valueOf(shop.getId()));
        // Return store id
        return Result.ok(shop.getId());
    }
    
    @Override
    public Result queryById(Long id) {
        //Shop shop = queryByIdV1(id);

        // Mutex lock solves cache breakdown
        //shop = queryByIdV2(id);

        // Logical expiration solves cache breakdown
        //shop = queryByIdV3(id);
        
        // 🚀The perfect solution! Use double detection locks and null value configuration to solve cache penetration and cache penetration
        Shop shop = queryByIdV4(id);
        
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }
        // 7.Return
        return Result.ok(shop);
    }
    
    public Shop queryByIdV1(Long id){
        return cacheClient
                .queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
    }
    
    public Shop queryByIdV2(Long id){
        return cacheClient
                .queryWithMutex(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
    }
    
    public Shop queryByIdV3(Long id){
        return cacheClient
                .queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, this::getById, 20L, TimeUnit.SECONDS);
    }
    
    public Shop queryByIdV4(Long id){
        Shop shop = null;
        //Local cache check
        RedisKeyBuild shopRedisKey = RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY, id);
        Shop localCacheHit = shopLocalCache.get(shopRedisKey.getRelKey());
        if(Objects.nonNull(localCacheHit)){
            return localCacheHit;
        }

        shop = redisCache.get(shopRedisKey, Shop.class);
        if (Objects.nonNull(shop)) {
            shopLocalCache.put(shopRedisKey.getRelKey(), shop);
            return shop;
        }
        log.info("查询商铺 从Redis缓存没有查询到 商铺id : {}",id);
        if (!bloomFilterHandlerFactory.get(BLOOM_FILTER_HANDLER_SHOP).contains(String.valueOf(id))) {
            log.info("查询商铺 布隆过滤器判断不存在 商铺id : {}",id);
            throw new RuntimeException("查询商铺不存在");
        }
        Boolean existResult = redisCache.hasKey(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY_NULL, id));
        if (existResult){
            throw new RuntimeException("查询商铺不存在");
        }
        RLock lock = serviceLockTool.getLock(LockType.Reentrant, LOCK_SHOP_KEY, new String[]{String.valueOf(id)});
        lock.lock();
        try {
            existResult = redisCache.hasKey(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY_NULL, id));
            if (existResult){
                throw new RuntimeException("查询商铺不存在");
            }
            //double-checing local cache and redis logic
            localCacheHit = shopLocalCache.get(shopRedisKey.getRelKey());
            if(Objects.nonNull(localCacheHit)){
                return localCacheHit;
            }
            shop = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY, id), Shop.class);
            if (Objects.nonNull(shop)) {
                shopLocalCache.put(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY, id).getRelKey(), shop);
                return shop;
            }
            shop = getById(id);
            if (Objects.isNull(shop)) {
                redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY_NULL, id),
                        "这是一个空值",
                        CACHE_SHOP_TTL,
                        TimeUnit.MINUTES);
                throw new RuntimeException("查询商铺不存在");
            }
            shopLocalCache.put(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY, id).getRelKey(), shop);
            redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.CACHE_SHOP_KEY, id),shop,
                    CACHE_SHOP_TTL,
                    TimeUnit.MINUTES);
            return shop;
        }finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        // 1. Update the database
        updateById(shop);
        // 2. Delete cache
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 1. Determine whether it is necessary to query based on coordinates
        //TODO is first changed so that both x and y are empty.
        x = null;
        y = null;
        if (x == null || y == null) {
            // No need for coordinate query, query according to database
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // Return data
            return Result.ok(page.getRecords());
        }

        // 2. Calculate paging parameters
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        // 3. Query redis, sort by distance, and paging. Result: shopId,distance
        String key = SHOP_GEO_KEY + typeId;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
                .search(
                        key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        // 4. Parse out the id
        if (results == null) {
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            // There is no next page, the end
            return Result.ok(Collections.emptyList());
        }
        // 4.1. Intercept the part from ~ end
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            // 4.2. Get store id
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            // 4.3. Get distance
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });
        // 5. Query Shop based on ID
        String idStr = StrUtil.join(",", ids);
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        // 6.Return
        return Result.ok(shops);
    }
}
