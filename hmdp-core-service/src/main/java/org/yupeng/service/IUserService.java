package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.dto.LoginFormDTO;
import org.yupeng.dto.Result;
import org.yupeng.entity.User;
import jakarta.servlet.http.HttpSession;


/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User interface
 * @author: yupeng
 **/
public interface IUserService extends IService<User> {

    Result<String> sendCode(String phone, HttpSession session);

    Result<String> login(LoginFormDTO loginForm, HttpSession session);

    Result<Void> sign();

    Result<Integer> signCount();

}
