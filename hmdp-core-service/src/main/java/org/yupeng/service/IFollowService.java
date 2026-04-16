package org.yupeng.service;

import org.yupeng.dto.Result;
import org.yupeng.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Follow interface
 * @author: yupeng
 **/
public interface IFollowService extends IService<Follow> {

    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);

    Result followCommons(Long id);
}
