package org.yupeng.utils;

import org.yupeng.dto.UserDTO;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User holder - used by Dark Horse Dianping regular version and plus version
 * @author: yupeng
 **/
public class UserHolder {
    private static final ThreadLocal<UserDTO> TL = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        TL.set(user);
    }

    public static UserDTO getUser(){
        return TL.get();
    }

    public static void removeUser(){
        TL.remove();
    }
}
