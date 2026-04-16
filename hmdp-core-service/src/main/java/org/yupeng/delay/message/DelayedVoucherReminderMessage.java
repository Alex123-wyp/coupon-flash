package org.yupeng.delay.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Sale-start reminder message DTO
 * @author: yupeng
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayedVoucherReminderMessage {
    
    private Long voucherId;
    
    private LocalDateTime beginTime;
}