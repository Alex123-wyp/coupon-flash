package org.yupeng.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher order
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class VoucherOrderDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    private Long id;

    /**
     * User ID for placing the order
     */
    private Long userId;

    /**
     * Voucher ID purchased
     */
    private Long voucherId;
    
    private String messageId;
    
    private Boolean autoIssue;

}
