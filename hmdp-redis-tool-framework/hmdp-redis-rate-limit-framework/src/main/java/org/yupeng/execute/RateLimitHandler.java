package org.yupeng.execute;
import org.yupeng.ratelimit.extension.RateLimitScene;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 限流执行 接口
 * @author: yupeng
 **/
public interface RateLimitHandler {
   
    void execute(Long voucherId, Long userId, RateLimitScene scene);
}
