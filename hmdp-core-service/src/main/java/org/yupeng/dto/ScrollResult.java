package org.yupeng.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Scroll result
 * @author: yupeng
 **/
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
