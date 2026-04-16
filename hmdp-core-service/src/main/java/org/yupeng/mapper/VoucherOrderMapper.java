package org.yupeng.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.yupeng.entity.VoucherOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Voucher order Mapper
 * @author: yupeng
 **/
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
    
    /**
     * Delete data based on voucher ID and user ID
     * @param voucherId voucher ID
     * @param userId user ID
     * @return delete quantity
     */
    @Delete("DELETE FROM tb_voucher_order where voucher_id = #{voucherId} and user_id = #{userId}")
    Integer deleteVoucherOrder(@Param("voucherId")Long voucherId, @Param("userId")Long userId);

}
