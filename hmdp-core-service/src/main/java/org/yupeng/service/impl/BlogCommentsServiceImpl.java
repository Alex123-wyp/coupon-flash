package org.yupeng.service.impl;

import org.yupeng.entity.BlogComments;
import org.yupeng.mapper.BlogCommentsMapper;
import org.yupeng.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Blog comment interface implementation
 * @author: yupeng
 **/
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
