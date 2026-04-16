package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.dto.DelayVoucherReminderDto;
import org.yupeng.dto.Result;
import org.yupeng.dto.SeckillVoucherDto;
import org.yupeng.dto.UpdateSeckillVoucherDto;
import org.yupeng.dto.UpdateSeckillVoucherStockDto;
import org.yupeng.dto.VoucherDto;
import org.yupeng.dto.VoucherSubscribeBatchDto;
import org.yupeng.dto.VoucherSubscribeDto;
import org.yupeng.entity.Voucher;
import org.yupeng.vo.GetSubscribeStatusVo;

import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher interface
 * @author: yupeng
 **/
public interface IVoucherService extends IService<Voucher> {

    Long addVoucher(VoucherDto voucherDto);
    
    Result<List<Voucher>> queryVoucherOfShop(Long shopId);

    Long addSeckillVoucher(SeckillVoucherDto seckillVoucherDto);
    
    void updateSeckillVoucher(UpdateSeckillVoucherDto updateSeckillVoucherDto);
    
    void updateSeckillVoucherStock(UpdateSeckillVoucherStockDto updateSeckillVoucherDto);
    
    void subscribe(VoucherSubscribeDto voucherSubscribeDto);
    
    void unsubscribe(VoucherSubscribeDto voucherSubscribeDto);
    
    Integer getSubscribeStatus(VoucherSubscribeDto voucherSubscribeDto);
    
    List<GetSubscribeStatusVo> getSubscribeStatusBatch(VoucherSubscribeBatchDto voucherSubscribeBatchDto);
    
    void delayVoucherReminder(DelayVoucherReminderDto delayVoucherReminderDto);
}
