package org.yupeng.service;

import org.yupeng.dto.Result;
import org.yupeng.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Shop interface
 * @author: yupeng
 **/
public interface IShopService extends IService<Shop> {

    Result saveShop(Shop shop);
    
    Result queryById(Long id);

    Result update(Shop shop);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
