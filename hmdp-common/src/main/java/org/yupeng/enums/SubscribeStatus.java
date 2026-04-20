package org.yupeng.enums;

import lombok.Getter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Whether to delete flash sale coupon order records
 * @author: yupeng
 **/
public enum SubscribeStatus {
    /**
     * Whether to delete flash sale coupon order records
     * */
    UNSUBSCRIBED(0, "Unsubscribed or not subscribed"),
    
    SUBSCRIBED(1, "Subscribed to voucher reminders (in queue)"),
    
    SUCCESS(2,"Auto-issue succeeded (order created)")
    ;
    
    @Getter
    private final Integer code;
    
    private String msg = "";
    
    SubscribeStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
        for (SubscribeStatus re : SubscribeStatus.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }
    
    public static SubscribeStatus getRc(Integer code) {
        for (SubscribeStatus re : SubscribeStatus.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
