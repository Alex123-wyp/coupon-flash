package org.javaup.util;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料 
 * @description: 分布式锁 方法类型执行 有返回值的业务
 * @author: yupeng
 **/
@FunctionalInterface
public interface TaskCall<V> {

    /**
     * 执行任务
     * @return 结果
     * */
    V call();
}
