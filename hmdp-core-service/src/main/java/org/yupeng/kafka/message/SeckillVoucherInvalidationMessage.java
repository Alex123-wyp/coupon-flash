package org.yupeng.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Broadcast message for seckill voucher cache invalidation
 * @author: yupeng
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillVoucherInvalidationMessage {
    private Long voucherId;
    
    private String reason;
}