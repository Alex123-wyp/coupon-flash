package org.yupeng.enums;

import lombok.Getter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: API response code
 * @author: yupeng
 **/
public enum BaseCode {
    /**
     * Basic code
     * */
    SUCCESS(0, "OK"),
    
    SECKILL_VOUCHER_NOT_EXIST(10001, "Seckill voucher does not exist"),
    
    SECKILL_VOUCHER_NOT_BEGIN(10002, "Seckill voucher has not started"),
    
    SECKILL_VOUCHER_IS_OVER(10003, "Seckill voucher has ended"),
    
    SECKILL_VOUCHER_STOCK_NOT_EXIST(10004, "Seckill voucher stock does not exist"),
    
    SECKILL_VOUCHER_STOCK_INSUFFICIENT(10005, "Insufficient seckill voucher stock"),
    
    SECKILL_VOUCHER_CLAIM(10006, "Seckill voucher has already been claimed"),
    
    SECKILL_RATE_LIMIT_IP_EXCEEDED(10007, "Requests are too frequent, please try again later"),
    
    SECKILL_RATE_LIMIT_USER_EXCEEDED(10008, "Operations are too frequent, please try again later"),
    
    SECKILL_VOUCHER_ORDER_NOT_EXIST(10009, "Voucher order does not exist"),
    
    AFTER_SECKILL_VOUCHER_REMAIN_STOCK_NOT_NEGATIVE_NUMBER(10010,"Remaining stock after the update cannot be negative"),
    
    VOUCHER_UNAVAILABLE(10011,"Voucher is unavailable"),
    
    VOUCHER_EXPIRED(10012,"Voucher has expired"),
    
    VOUCHER_ORDER_EXIST(10013,"Voucher order already exists"),
    
    VOUCHER_ORDER_CANCEL(10014,"Voucher order has been canceled"),
    
    USER_NOT_EXIST(20000, "User does not exist"),
    
    USER_ALREADY_PURCHASE(20001, "User has already purchased"),
    ;
    
    @Getter
    private final Integer code;
    
    private String msg = "";
    
    BaseCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
//        for (BaseCode re : BaseCode.values()) {
//            if (re.code.intValue() == code.intValue()) {
//                return re.msg;
//            }
//        }
//        return "";
        for(BaseCode re : BaseCode.values()){
            if(re.code.intValue() == code.intValue()){
                return re.msg;
            }
        }
        return "";
    }
    
    public static BaseCode getRc(Integer code) {
//        for (BaseCode re : BaseCode.values()) {
//            if (re.code.intValue() == code.intValue()) {
//                return re;
//            }
//        }
//        return null;

        for(BaseCode rc : BaseCode.values()){
            if(rc.code.intValue() == code.intValue()){
                return rc;
            }
        }
        return null;
    }
}
