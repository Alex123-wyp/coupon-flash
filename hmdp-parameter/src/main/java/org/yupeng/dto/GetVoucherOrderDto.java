package org.yupeng.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Get voucher order
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class GetVoucherOrderDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * order ID
     */
    @NotNull
    private Long orderId;

}
