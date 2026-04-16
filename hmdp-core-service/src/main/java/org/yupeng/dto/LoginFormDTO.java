package org.yupeng.dto;

import lombok.Data;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Login input DTO
 * @author: yupeng
 **/
@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
