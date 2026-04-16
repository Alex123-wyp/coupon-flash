package org.yupeng.service;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Token
 * @author: yupeng
 **/
public interface ISeckillAccessTokenService {
  
    boolean isEnabled();
 
    String issueAccessToken(Long voucherId, Long userId);
    
    boolean validateAndConsume(Long voucherId, Long userId, String token);
}