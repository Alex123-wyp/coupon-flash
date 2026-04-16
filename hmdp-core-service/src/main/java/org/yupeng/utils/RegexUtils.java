package org.yupeng.utils;

import cn.hutool.core.util.StrUtil;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Regex utilities for the regular and plus HMDP versions
 * @author: yupeng
 **/
public class RegexUtils {
    /**
     * Is it an invalid mobile phone format?
     * @param phone The mobile phone number to be verified
     * @return true: consistent, false: not consistent
     */
    public static boolean isPhoneInvalid(String phone){
        return mismatch(phone, RegexPatterns.PHONE_REGEX);
    }
    /**
     * Is it an invalid email format?
     * @param email The email address to be verified
     * @return true: consistent, false: not consistent
     */
    public static boolean isEmailInvalid(String email){
        return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }

    /**
     * Is it an invalid verification code format?
     * @param code Verification code to be verified
     * @return true: consistent, false: not consistent
     */
    public static boolean isCodeInvalid(String code){
        return mismatch(code, RegexPatterns.VERIFY_CODE_REGEX);
    }

    // Check whether it does not conform to the regular format
    private static boolean mismatch(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
