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
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher order routing implementation interface
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
