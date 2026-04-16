package org.yupeng.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Query seckill voucher
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GetSeckillVoucherDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * voucher ID
     */
    @NotNull
    private Long voucherId;
}
