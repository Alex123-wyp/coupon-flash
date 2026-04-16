package org.yupeng.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.yupeng.dto.Result;
import org.yupeng.entity.Shop;
import org.yupeng.service.IShopService;
import org.yupeng.utils.SystemConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Shop API
 * @author: yupeng
 **/
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    public IShopService shopService;

    /**
     * Query store information based on ID
     * @param id shop ID
     * @return store details data
     */
    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        return shopService.queryById(id);
    }

    /**
     * Add store information
     * @param shop shop data
     * @return shop ID
     */
    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        return shopService.saveShop(shop);
    }

    /**
     * Update store information
     * @param shop shop data
     * @return None
     */
    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        // Write to database
        return shopService.update(shop);
    }

    /**
     * Query store information by page according to store type
     * @param typeId store type
     * @param current page number
     * @return store list
     */
    @GetMapping("/of/type")
    public Result queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Double y
    ) {
       return shopService.queryShopByType(typeId, current, x, y);
    }

    /**
     * Query store information by page based on store name keywords
     * @param name store name keyword
     * @param current page number
     * @return store list
     */
    @GetMapping("/of/name")
    public Result queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // Paging query based on type
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // Return data
        return Result.ok(page.getRecords());
    }
}
