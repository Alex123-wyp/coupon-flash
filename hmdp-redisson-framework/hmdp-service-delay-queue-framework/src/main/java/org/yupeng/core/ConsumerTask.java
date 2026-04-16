package org.yupeng.core;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Delay queue consumer interface
 * @author: yupeng
 **/
public interface ConsumerTask {
    
    void execute(String content);
  
    String topic();
}
