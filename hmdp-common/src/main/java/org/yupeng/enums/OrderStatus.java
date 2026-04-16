package org.yupeng.enums;

import lombok.Getter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Order status
 * @author: yupeng
 **/
public enum OrderStatus {
    /**
     * Order status
     * */
    NORMAL(1, "正常"),
    
    CANCEL(2, "取消"),
    ;
    
    @Getter
    private final Integer code;
    
    private String msg = "";
    
    OrderStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
        for (OrderStatus re : OrderStatus.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }
    
    public static OrderStatus getRc(Integer code) {
        for (OrderStatus re : OrderStatus.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
