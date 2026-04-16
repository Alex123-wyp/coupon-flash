package org.yupeng.service;

import org.yupeng.dto.Result;
import org.yupeng.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Blog interface
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
