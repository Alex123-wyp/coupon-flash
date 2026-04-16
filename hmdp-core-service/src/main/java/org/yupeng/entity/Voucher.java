package org.yupeng.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "id")
    private Long id;

    /**
     * shop ID
     */
    private Long shopId;

    /**
     * Voucher title
     */
    private String title;

    /**
     * subtitle
     */
    private String subTitle;

    /**
     * Usage rules
     */
    private String rules;

    /**
     * Payment amount
     */
    private Long payValue;

    /**
     * Deduction amount
     */
    private Long actualValue;

    /**
     * Coupon type
     */
    private Integer type;

    /**
     * Coupon status 1, on the shelves; 2, off the shelves; 3, expired
     */
    private Integer status;
    /**
     * in stock
     */
    @TableField(exist = false)
    private Integer stock;

    /**
     * effective time
     */
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /**
     * expiration time
     */
    @TableField(exist = false)
    private LocalDateTime endTime;

    /**
     * create time
     */
    private LocalDateTime createTime;


    /**
     * update time
     */
    private LocalDateTime updateTime;


}
