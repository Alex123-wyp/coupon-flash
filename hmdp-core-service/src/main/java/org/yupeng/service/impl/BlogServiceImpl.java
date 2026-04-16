package org.yupeng.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.yupeng.dto.Result;
import org.yupeng.dto.ScrollResult;
import org.yupeng.dto.UserDTO;
import org.yupeng.entity.Blog;
import org.yupeng.entity.Follow;
import org.yupeng.entity.User;
import org.yupeng.mapper.BlogMapper;
import org.yupeng.service.IBlogService;
import org.yupeng.service.IFollowService;
import org.yupeng.service.IUserService;
import org.yupeng.toolkit.SnowflakeIdGenerator;
import org.yupeng.utils.SystemConstants;
import org.yupeng.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.yupeng.utils.RedisConstants.BLOG_LIKED_KEY;
import static org.yupeng.utils.RedisConstants.FEED_KEY;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Blog interface implementation
 * @author: yupeng
 **/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IFollowService followService;
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public Result queryHotBlog(Integer current) {
        // Based on user query
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // Get current page data
        List<Blog> records = page.getRecords();
        // Query user
        records.forEach(blog -> {
            this.queryBlogUser(blog);
            this.isBlogLiked(blog);
        });
        return Result.ok(records);
    }

    @Override
    public Result queryBlogById(Long id) {
        // 1. Query blog
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("笔记不存在！");
        }
        // 2. Query users related to blog
        queryBlogUser(blog);
        // 3. Check whether the blog has been liked
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    private void isBlogLiked(Blog blog) {
        // 1. Get the logged in user
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            // The user is not logged in, so there is no need to check whether to like or not.
            return;
        }
        Long userId = user.getId();
        // 2. Determine whether the currently logged in user has liked it
        String key = "blog:liked:" + blog.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        blog.setIsLike(score != null);
    }

    @Override
    public Result likeBlog(Long id) {
        // 1. Get the logged in user
        Long userId = UserHolder.getUser().getId();
        // 2. Determine whether the currently logged in user has liked it
        String key = BLOG_LIKED_KEY + id;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null) {
            // 3. If you haven’t liked it, you can like it
            // 3.1. Number of likes in database + 1
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            // 3.2. Save the user to the Redis set collection zadd key value score
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else {
            // 4. If you have liked it, cancel the like
            // 4.1. Number of likes in database -1
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            // 4.2. Remove the user from the Redis set collection
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result queryBlogLikes(Long id) {
        String key = BLOG_LIKED_KEY + id;
        // 1. Query the top 5 likes users zrange key 0 4
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        // 2. Parse out the user ID
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        // 3. Query the user based on user ID WHERE id IN (5, 1) ORDER BY FIELD(id, 5, 1)
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        // 4.Return
        return Result.ok(userDTOS);
    }

    @Override
    public Result saveBlog(Blog blog) {
        // 1. Get the logged in user
        UserDTO user = UserHolder.getUser();
        blog.setId(snowflakeIdGenerator.nextId());
        blog.setUserId(user.getId());
        // 2. Save store visit notes
        boolean isSuccess = save(blog);
        if(!isSuccess){
            return Result.fail("新增笔记失败!");
        }
        // 3. Query all fans of the note author select * from tb_follow where follow_user_id = ?
        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
        // 4. Push note ID to all fans
        for (Follow follow : follows) {
            // 4.1. Get fan ID
            Long userId = follow.getUserId();
            // 4.2.Push
            String key = FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        // 5.Return id
        return Result.ok(blog.getId());
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        // 1. Get the current user
        Long userId = UserHolder.getUser().getId();
        // 2. Query the inbox ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3. Non-empty judgment
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.ok();
        }
        // 4. Parse data: blogId, minTime (timestamp), offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1. Get id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2. Get score (timestamp)
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5. Query blog based on ID
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        for (Blog blog : blogs) {
            // 5.1. Query users related to blog
            queryBlogUser(blog);
            // 5.2. Check whether the blog has been liked
            isBlogLiked(blog);
        }

        // 6. Encapsulate and return
        ScrollResult r = new ScrollResult();
        r.setList(blogs);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.ok(r);
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }
}
