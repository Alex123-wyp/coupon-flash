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
 * @description: Shop
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "id")
    private Long id;

    /**
     * Store name
     */
    private String name;

    /**
     * ID of store type
     */
    private Long typeId;

    /**
     * Store pictures, multiple pictures separated by ','
     */
    private String images;

    /**
     * Business districts, such as Lujiazui
     */
    private String area;

    /**
     * address
     */
    private String address;

    /**
     * longitude
     */
    private Double x;

    /**
     * Dimensions
     */
    private Double y;

    /**
     * Average price, rounded to an integer
     */
    private Long avgPrice;

    /**
     * Sales volume
     */
    private Integer sold;

    /**
     * Number of comments
     */
    private Integer comments;

    /**
     * Rating, 1~5 points, multiply by 10 to save, avoid decimals
     */
    private Integer score;

    /**
     * Business hours, for example 10:00-22:00
     */
    private String openHours;

    /**
     * create time
     */
    private LocalDateTime createTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;


    @TableField(exist = false)
    private Double distance;
}
