package org.yupeng.enums;

import lombok.Getter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Business type
 * @author: yupeng
 **/
public enum BusinessType {
    /**
     * Business type
     * */
    SUCCESS(1, "Order created successfully"),
    TIMEOUT(2, "Order creation timed out"),
    FAIL(3, "Order creation failed"),
    CANCEL(4, "Canceled manually"),
   
    
    ;
    
    @Getter
    private final Integer code;
    
    private String msg = "";
    
    BusinessType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
        for (BusinessType re : BusinessType.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }
    
    public static BusinessType getRc(Integer code) {
        for (BusinessType re : BusinessType.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
