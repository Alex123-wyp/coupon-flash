package org.yupeng.init;

import cn.hutool.core.date.LocalDateTimeUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.entity.SeckillVoucher;
import org.yupeng.service.ISeckillVoucherService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Seckill voucher data reset-start and end time
 * @author: yupeng
 **/
@Slf4j
@Order(2)
@Component
public class SeckillVoucherDataRenewalInit {
    
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    
    @PostConstruct
    public void init(){
        updateBeginAndEndTime();
        //Restore inventory quantity
        //renewalStock();
    }
    
    public void updateBeginAndEndTime(){
        log.info("==========更新优惠券的开始时间和结束时间==========");
        //Query program performance data whose coupon end time is less than 2 days ago
        List<SeckillVoucher> seckillVoucherList =
                seckillVoucherService.lambdaQuery()
                        .le(SeckillVoucher::getEndTime,
                                LocalDateTimeUtil.offset(LocalDateTimeUtil.now(), 2, ChronoUnit.DAYS))
                        .list();
        for (SeckillVoucher seckillVoucher : seckillVoucherList) {
            LocalDateTime oldBeginTime = seckillVoucher.getBeginTime();
            LocalDateTime oldEndTime = seckillVoucher.getEndTime();
            //Add 15 days to the existing start time as the new start time
            LocalDateTime newBeginTime = LocalDateTimeUtil.offset(oldBeginTime, 15, ChronoUnit.DAYS);
            //Add 15 days to the existing end time as the new end time
            LocalDateTime newEndTime = LocalDateTimeUtil.offset(oldEndTime, 15, ChronoUnit.DAYS);
            LocalDateTime nowTime = LocalDateTimeUtil.now();
            //If the new end time is still less than the current time, continue for another day until the new end time is greater than the current time.
            while (newEndTime.isBefore(nowTime)) {
                newBeginTime = LocalDateTimeUtil.offset(newBeginTime,1,ChronoUnit.DAYS);
                newEndTime = LocalDateTimeUtil.offset(newEndTime,1,ChronoUnit.DAYS);
            }
            //perform update
            seckillVoucherService.lambdaUpdate()
                    .set(SeckillVoucher::getBeginTime, newBeginTime)
                    .set(SeckillVoucher::getEndTime, newEndTime)
                    .set(SeckillVoucher::getUpdateTime,LocalDateTimeUtil.now())
                    .eq(SeckillVoucher::getId,seckillVoucher.getId())
                    .eq(SeckillVoucher::getVoucherId,seckillVoucher.getVoucherId())
                    .update();
        }
    }
    
    public void renewalStock(){
        log.info("==========将优惠券的库存数量恢复==========");
        //Restore inventory quantity
        List<SeckillVoucher> seckillVoucherList = seckillVoucherService.list();
        for (SeckillVoucher seckillVoucher : seckillVoucherList) {
            if (!seckillVoucher.getInitStock().equals(seckillVoucher.getStock())) {
                //perform update
                seckillVoucherService.lambdaUpdate()
                        .set(SeckillVoucher::getStock,seckillVoucher.getInitStock())
                        .set(SeckillVoucher::getUpdateTime,LocalDateTimeUtil.now())
                        .eq(SeckillVoucher::getId,seckillVoucher.getId())
                        .eq(SeckillVoucher::getVoucherId,seckillVoucher.getVoucherId())
                        .update();
            }
        }
    }
}
