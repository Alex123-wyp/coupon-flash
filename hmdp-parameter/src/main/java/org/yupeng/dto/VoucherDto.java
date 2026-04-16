package org.yupeng.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VoucherDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * shop ID
     */
    @NotNull
    private Long shopId;

    /**
     * Voucher title
     */
    @NotBlank
    private String title;

    /**
     * subtitle
     */
    @NotBlank
    private String subTitle;

    /**
     * Usage rules
     */
    @NotBlank
    private String rules;

    /**
     * Payment amount
     */
    @NotNull
    private Long payValue;

    /**
     * Deduction amount
     */
    @NotNull
    private Long actualValue;

    /**
     * Coupon type 0, ordinary coupon; 1, flash sale coupon
     */
    @NotNull
    private Integer type;

    /**
     * Coupon status 1, on the shelves; 2, off the shelves; 3, expired
     */
    @NotNull
    private Integer status;


}
