package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.dto.VoucherReconcileLogDto;
import org.yupeng.entity.VoucherReconcileLog;
import org.yupeng.kafka.message.SeckillVoucherMessage;
import org.yupeng.message.MessageExtend;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Reconciliation log interface
 * @author: yupeng
 **/
public interface IVoucherReconcileLogService extends IService<VoucherReconcileLog> {
    
    boolean saveReconcileLog(Integer logType,
                             Integer businessType,
                             String detail,
                             MessageExtend<SeckillVoucherMessage> message);
    
    boolean saveReconcileLog(Integer logType,
                             Integer businessType,
                             String detail,
                             Long traceId,
                             MessageExtend<SeckillVoucherMessage> message);
    
    
    boolean saveReconcileLog(VoucherReconcileLogDto voucherReconcileLogDto);
}