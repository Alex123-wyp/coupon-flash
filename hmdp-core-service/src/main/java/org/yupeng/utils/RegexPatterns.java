package org.yupeng.utils;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Regex constants for the regular and plus HMDP versions
 * @author: yupeng
 **/
public abstract class RegexPatterns {
    /**
     * Regular mobile phone number
     */
    public static final String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    /**
     * Email regular
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * Password regularity. 4~32 digits of letters, numbers, and underscores
     */
    public static final String PASSWORD_REGEX = "^\\w{4,32}$";
    /**
     * Verification code is regular, 6 digits or letters
     */
    public static final String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";

}
