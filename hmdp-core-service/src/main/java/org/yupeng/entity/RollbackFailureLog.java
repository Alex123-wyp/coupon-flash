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
 * @description: Rollback failure log entity
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_rollback_failure_log")
public class RollbackFailureLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Primary key */
    @TableId(value = "id")
    private Long id;

    /** voucher ID */
    private Long voucherId;

    /** user ID */
    private Long userId;

    /** order ID */
    private Long orderId;

    /** Track unique identifiers */
    private Long traceId;

    /** Reason or details of failure */
    private String detail;

    /** Lua return code (BaseCode), used to determine the failure type */
    private Integer resultCode;

    /**Number of retries attempted */
    private Integer retryAttempts;

    /** Source component, for example: redis_voucher_data/producer */
    private String source;

    /** create time */
    private LocalDateTime createTime;

    /** update time */
    private LocalDateTime updateTime;
}