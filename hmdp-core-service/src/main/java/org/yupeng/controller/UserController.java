package org.yupeng.controller;


import cn.hutool.core.bean.BeanUtil;
import org.yupeng.dto.LoginFormDTO;
import org.yupeng.dto.Result;
import org.yupeng.dto.UserDTO;
import org.yupeng.entity.User;
import org.yupeng.entity.UserInfo;
import org.yupeng.service.IUserInfoService;
import org.yupeng.service.IUserService;
import org.yupeng.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User API
 * @author: yupeng
 **/
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * Send mobile phone verification code
     */
    //In Spring MVC, if HttpSession has been declared in controller layer or service entry method parameter, everytime access, Spring will resolve it from
    //the current Http request automatically
    @PostMapping("code")
    public Result<String> sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // Send SMS verification code and save verification code
        return userService.sendCode(phone, session);
    }

    /**
     * Login function
     * @param loginForm login parameters, including mobile phone number and verification code; or mobile phone number and password
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        // Implement login function
        return userService.login(loginForm, session);
    }

    /**
     * Logout function
     * @return None
     */
    @PostMapping("/logout")
    public Result<Void> logout(){
        // TODO implements the logout function
        return Result.fail("Feature not implemented");
    }

    @GetMapping("/me")
    public Result<UserDTO> me(){
        // Get the currently logged in user and return
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result<UserInfo> info(@PathVariable("id") String userId){
        // Query details
        UserInfo info = userInfoService.getById(Long.parseLong(userId));
        if (info == null) {
            // There are no details. It should be the first time to check the details.
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // return
        return Result.ok(info);
    }

    /**
     * Current logged in user update level
     */
    @PostMapping("/level/update")
    public Result<Void> updateLevel(@RequestParam("newLevel") Integer newLevel) {
        UserDTO current = UserHolder.getUser();
        if (Objects.isNull(current)) {
            return Result.fail("Not logged in");
        }
        return userInfoService.updateUserLevel(current.getId(), newLevel);
    }

    @GetMapping("/{id}")
    public Result<UserDTO> queryUserById(@PathVariable("id") Long userId){
        // Query details
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // return
        return Result.ok(userDTO);
    }

    @PostMapping("/sign")
    public Result<Void> sign(){
        return userService.sign();
    }

    @GetMapping("/sign/count")
    public Result signCount(){
        return userService.signCount();
    }
}
