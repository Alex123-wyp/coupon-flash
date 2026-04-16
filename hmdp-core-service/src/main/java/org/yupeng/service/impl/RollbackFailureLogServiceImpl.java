package org.yupeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.yupeng.entity.RollbackFailureLog;
import org.yupeng.mapper.RollbackFailureLogMapper;
import org.yupeng.service.IRollbackFailureLogService;
import org.springframework.stereotype.Service;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Rollback failure log interface implementation
 * @author: yupeng
 **/
@Service
public class RollbackFailureLogServiceImpl extends ServiceImpl<RollbackFailureLogMapper, RollbackFailureLog>
        implements IRollbackFailureLogService {
}