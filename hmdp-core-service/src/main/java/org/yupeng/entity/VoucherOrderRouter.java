package org.yupeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Seckill voucher order route
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher_order_router")
public class VoucherOrderRouter implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id")
    private Long id;

    private Long orderId;
    
    private Long userId;

    private Long voucherId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;


}
