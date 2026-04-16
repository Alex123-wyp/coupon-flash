package org.yupeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.yupeng.entity.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher Mapper
 * @author: yupeng
 **/
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
