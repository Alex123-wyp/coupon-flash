package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.dto.CancelVoucherOrderDto;
import org.yupeng.dto.GetVoucherOrderByVoucherIdDto;
import org.yupeng.dto.GetVoucherOrderDto;
import org.yupeng.dto.Result;
import org.yupeng.entity.VoucherOrder;
import org.yupeng.kafka.message.SeckillVoucherMessage;
import org.yupeng.message.MessageExtend;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher order interface
 * @author: yupeng
 **/
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result<Long> seckillVoucher(Long voucherId);

    void createVoucherOrderV1(VoucherOrder voucherOrder);
    
    boolean createVoucherOrderV2(MessageExtend<SeckillVoucherMessage> message);
    
    Long getSeckillVoucherOrder(GetVoucherOrderDto getVoucherOrderDto);
    
    Boolean cancel(CancelVoucherOrderDto cancelVoucherOrderDto);
    
    boolean autoIssueVoucherToEarliestSubscriber(final Long voucherId, final Long excludeUserId);
    
    Long getSeckillVoucherOrderIdByVoucherId(GetVoucherOrderByVoucherIdDto getVoucherOrderByVoucherIdDto);
}
