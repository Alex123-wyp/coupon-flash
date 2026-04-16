package org.yupeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Seckill voucher table has a one-to-one relationship with coupons
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_seckill_voucher")
public class SeckillVoucher implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * primary key
     */
    @TableId(value = "id")
    private Long id;
    
    /**
     * The id of the associated coupon
     */
    private Long voucherId;
    
    /**
     * InitializeInventory
     */
    private Integer initStock;

    /**
     * in stock
     */
    private Integer stock;

    /**
     * Member levels allowed to participate, separated by commas, such as: "1,2,3"
     */
    private String allowedLevels;

    /**
     * Minimum membership level
     */
    private Integer minLevel;

    /**
     * create time
     */
    private LocalDateTime createTime;

    /**
     * effective time
     */
    private LocalDateTime beginTime;

    /**
     * expiration time
     */
    private LocalDateTime endTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;


}
