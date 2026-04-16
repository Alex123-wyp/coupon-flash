package org.yupeng.enums;

import lombok.Getter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Stock operation type
 * @author: yupeng
 **/
public enum StockUpdateType {
    /**
     * Inventory operation type
     * */
    DECREASE(-1, "扣减"),
    
    INCREASE(1, "增加"),
    ;
    
    @Getter
    private final Integer code;
    
    private String msg = "";
    
    StockUpdateType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
        for (StockUpdateType re : StockUpdateType.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }
    
    public static StockUpdateType getRc(Integer code) {
        for (StockUpdateType re : StockUpdateType.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
