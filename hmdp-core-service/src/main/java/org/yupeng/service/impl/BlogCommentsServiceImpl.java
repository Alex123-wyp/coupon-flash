package org.yupeng.service.impl;

import org.yupeng.entity.BlogComments;
import org.yupeng.mapper.BlogCommentsMapper;
import org.yupeng.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 博客评论接口实现
 * @author: yupeng
 **/
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
