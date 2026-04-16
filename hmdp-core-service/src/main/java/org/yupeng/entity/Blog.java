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
 * @description: Blog entity
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "id")
    private Long id;
    /**
     * merchant ID
     */
    private Long shopId;
    /**
     * user ID
     */
    private Long userId;
    /**
     * user icon
     */
    @TableField(exist = false)
    private String icon;
    /**
     * Username
     */
    @TableField(exist = false)
    private String name;
    /**
     * Have you liked it?
     */
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * title
     */
    private String title;

    /**
     * There are a maximum of 9 photos for store visit, multiple photos separated by ","
     */
    private String images;

    /**
     * Text description of Tandian
     */
    private String content;

    /**
     * Number of likes
     */
    private Integer liked;

    /**
     * Number of comments
     */
    private Integer comments;

    /**
     * create time
     */
    private LocalDateTime createTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;


}
