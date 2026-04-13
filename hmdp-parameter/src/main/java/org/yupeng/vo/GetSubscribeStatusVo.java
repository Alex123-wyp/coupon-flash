package org.yupeng.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 优惠券订阅状态
 * @author: yupeng
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class GetSubscribeStatusVo implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 优惠券id
     * */
    private Long voucherId;
    
    /**
     * 是否订阅 1：已订阅  0：没有订阅
     * */
    private Integer subscribeStatus;
}
