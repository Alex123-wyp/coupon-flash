package org.yupeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User information
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * primary key
     */
    @TableId(value = "id")
    private Long id;
    
    /**
     * user ID
     */
    private Long userId;

    /**
     * city ​​name
     */
    private String city;

    /**
     * Personal introduction, no more than 128 characters
     */
    private String introduce;

    /**
     * Number of fans
     */
    private Integer fans;

    /**
     * number of people following
     */
    private Integer followee;

    /**
     * Gender, 0: male, 1: female
     */
    private Boolean gender;

    /**
     * Birthday
     */
    private LocalDate birthday;

    /**
     * integral
     */
    private Integer credits;

    /**
     * Membership level, 0~9, 0 represents unactivated membership
     */
    private Integer level;

    /**
     * create time
     */
    private LocalDateTime createTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;


}
