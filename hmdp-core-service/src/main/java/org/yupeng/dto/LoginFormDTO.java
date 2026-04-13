package org.yupeng.dto;

import lombok.Data;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 登录-入参
 * @author: yupeng
 **/
@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
