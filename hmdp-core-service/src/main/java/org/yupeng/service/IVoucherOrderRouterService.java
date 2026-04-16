package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.dto.GetVoucherOrderRouterDto;
import org.yupeng.entity.VoucherOrderRouter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher order routing interface
 * @author: yupeng
 **/
public interface IVoucherOrderRouterService extends IService<VoucherOrderRouter> {
    
    Long get(GetVoucherOrderRouterDto getVoucherOrderRouterDto);
}
