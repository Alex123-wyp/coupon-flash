package org.yupeng.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher subscription batch query
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class VoucherSubscribeBatchDto implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * voucher ID collection
     * */
    @NotNull
    private List<Long> voucherIdList;
}
