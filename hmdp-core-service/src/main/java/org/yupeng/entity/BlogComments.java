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
 * @description: Blog comment entity
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog_comments")
public class BlogComments implements Serializable {

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
     * Tandian ID
     */
    private Long blogId;

    /**
     * The associated level 1 review id, if it is a level 1 review, the value is 0
     */
    private Long parentId;

    /**
     * Reply comment id
     */
    private Long answerId;

    /**
     * Reply content
     */
    private String content;

    /**
     * Number of likes
     */
    private Integer liked;

    /**
     * Status, 0: normal, 1: reported, 2: prohibited from viewing
     */
    private Boolean status;

    /**
     * create time
     */
    private LocalDateTime createTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;


}
