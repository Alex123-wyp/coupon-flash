package org.yupeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yupeng.dto.Result;
import org.yupeng.entity.UserInfo;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: User information interface
 * @author: yupeng
 **/
public interface IUserInfoService extends IService<UserInfo> {
    
    /**
     * Query user information by user ID
     * @param userId user ID
     * @return result
     */
    UserInfo getByUserId(Long userId);
    
    /**
     * Update user levels and maintain level inverted index collections
     * @param userId user ID
     * @param newLevel new level
     * @return result
     */
    Result<Void> updateUserLevel(Long userId, Integer newLevel);

}
