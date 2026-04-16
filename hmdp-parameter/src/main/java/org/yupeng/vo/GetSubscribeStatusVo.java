package org.yupeng.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher subscription status
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class GetSubscribeStatusVo implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * voucher ID
     * */
    private Long voucherId;
    
    /**
     * Whether to subscribe 1: Subscribed 0: Not subscribed
     * */
    private Integer subscribeStatus;
}
