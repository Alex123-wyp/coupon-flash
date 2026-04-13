package org.yupeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.yupeng.dto.GetVoucherOrderRouterDto;
import org.yupeng.entity.VoucherOrderRouter;
import org.yupeng.mapper.VoucherOrderRouterMapper;
import org.yupeng.service.IVoucherOrderRouterService;
import org.yupeng.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 优惠券订单路由实现 接口
 * @author: yupeng
 **/
@Slf4j
@Service
public class VoucherOrderRouterServiceImpl extends ServiceImpl<VoucherOrderRouterMapper, VoucherOrderRouter> implements IVoucherOrderRouterService {
    
    @Override
    public Long get(GetVoucherOrderRouterDto getVoucherOrderRouterDto) {
        VoucherOrderRouter voucherOrderRouter = lambdaQuery()
                .eq(VoucherOrderRouter::getUserId,  UserHolder.getUser().getId())
                .eq(VoucherOrderRouter::getVoucherId, getVoucherOrderRouterDto.getVoucherId())
                .one();
        if (Objects.nonNull(voucherOrderRouter)) {
            return voucherOrderRouter.getOrderId();
        }
        return null;
    }
}
