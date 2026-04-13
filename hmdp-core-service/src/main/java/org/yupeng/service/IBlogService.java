package org.yupeng.service;

import org.yupeng.dto.Result;
import org.yupeng.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 博客接口
 * @author: yupeng
 **/
public interface IBlogService extends IService<Blog> {

    Result queryHotBlog(Integer current);

    Result queryBlogById(Long id);

    Result likeBlog(Long id);

    Result queryBlogLikes(Long id);

    Result saveBlog(Blog blog);

    Result queryBlogOfFollow(Long max, Integer offset);

}
