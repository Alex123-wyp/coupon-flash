package org.javaup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaup.dto.GetVoucherOrderRouterDto;
import org.javaup.entity.VoucherOrderRouter;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 优惠券订单路由 接口
 * @author: yupeng
 **/
public interface IVoucherOrderRouterService extends IService<VoucherOrderRouter> {
    
    Long get(GetVoucherOrderRouterDto getVoucherOrderRouterDto);
}
