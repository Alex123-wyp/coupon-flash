package org.javaup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaup.entity.SeckillVoucher;
import org.javaup.model.SeckillVoucherFullModel;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 秒杀优惠券 接口
 * @author: yupeng
 **/
public interface ISeckillVoucherService extends IService<SeckillVoucher> {
    
    SeckillVoucherFullModel queryByVoucherId(Long voucherId);
    
    void loadVoucherStock(Long voucherId);
    
    boolean rollbackStock(Long voucherId);
}
