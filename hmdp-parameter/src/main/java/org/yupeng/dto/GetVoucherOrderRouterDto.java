package org.yupeng.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Get voucher order routing
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class GetVoucherOrderRouterDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * voucher ID
     */
    @NotNull
    private Long voucherId;
}
