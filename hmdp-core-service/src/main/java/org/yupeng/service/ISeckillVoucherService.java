package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.entity.SeckillVoucher;
import org.yupeng.model.SeckillVoucherFullModel;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Seckill voucher interface
 * @author: yupeng
 **/
public interface ISeckillVoucherService extends IService<SeckillVoucher> {
    
    SeckillVoucherFullModel queryByVoucherId(Long voucherId);
    
    void loadVoucherStock(Long voucherId);
    
    boolean rollbackStock(Long voucherId);
}
