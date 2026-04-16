package org.yupeng.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Seckill voucher
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SeckillVoucherDto implements Serializable {

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
     * Usage rules (normal coupons are available, flash sale audiences no longer rely on this field)
     */
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
    
    /**
     * in stock
     */
    @NotNull
    private Integer stock;
    
    /**
     * effective time
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;
    
    /**
     * expiration time
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * Member levels allowed to participate, separated by commas, such as: "1,2,3"
     */
    private String allowedLevels;

    /**
     * Minimum membership level
     */
    private Integer minLevel;
}
