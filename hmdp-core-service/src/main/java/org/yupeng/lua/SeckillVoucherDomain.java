package org.yupeng.lua;

import lombok.Data;

/**
 * @program: coupon-flash program
 * @description: Lua second kill returns value
 * @author: yupeng
 **/
@Data
public class SeckillVoucherDomain {


    private Integer code;
    
    private Integer beforeQty;
    
    private Integer deductQty;
    
    private Integer afterQty;


}
