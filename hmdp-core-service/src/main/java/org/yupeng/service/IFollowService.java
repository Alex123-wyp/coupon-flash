package org.yupeng.service;

import org.yupeng.dto.Result;
import org.yupeng.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 关注接口
 * @author: yupeng
 **/
public interface IFollowService extends IService<Follow> {

    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);

    Result followCommons(Long id);
}
