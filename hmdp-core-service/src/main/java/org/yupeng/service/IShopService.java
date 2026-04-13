package org.yupeng.service;

import org.yupeng.dto.Result;
import org.yupeng.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 商铺 接口
 * @author: yupeng
 **/
public interface IShopService extends IService<Shop> {

    Result saveShop(Shop shop);
    
    Result queryById(Long id);

    Result update(Shop shop);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
