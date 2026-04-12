package org.javaup.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 秒杀券缓存失效广播消息
 * @author: yupeng
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillVoucherInvalidationMessage {
    
    private Long voucherId;
    
    private String reason;
}