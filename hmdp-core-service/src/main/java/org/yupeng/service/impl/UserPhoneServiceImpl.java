package org.yupeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.entity.UserPhone;
import org.yupeng.mapper.UserPhoneMapper;
import org.yupeng.service.IUserPhoneService;
import org.springframework.stereotype.Service;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 用户手机 接口实现
 * @author: yupeng
 **/
@Slf4j
@Service
public class UserPhoneServiceImpl extends ServiceImpl<UserPhoneMapper, UserPhone> implements IUserPhoneService {
    
}
