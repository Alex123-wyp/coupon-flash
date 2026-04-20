package org.yupeng.enums;

import lombok.Getter;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Whether to delete flash sale coupon order records
 * @author: yupeng
 **/
public enum SeckillVoucherOrderOperate {
    /**
     * Whether to delete flash sale coupon order records
     * */
    NO(0, "Keep"),
    YES(1, "Delete"),
    ;
    
    @Getter
    private final Integer code;
    
    private String msg = "";
    
    SeckillVoucherOrderOperate(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
        for (SeckillVoucherOrderOperate re : SeckillVoucherOrderOperate.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }
    
    public static SeckillVoucherOrderOperate getRc(Integer code) {
        for (SeckillVoucherOrderOperate re : SeckillVoucherOrderOperate.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
