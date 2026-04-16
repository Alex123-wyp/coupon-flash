package org.yupeng.servicelock.info;


/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Strategy
 * @author: yupeng
 **/
public enum LockTimeOutStrategy implements LockTimeOutHandler{
    /**
     * fail fast
     * */
    FAIL(){
        @Override
        public void handler(String lockName) {
            String msg = String.format("%s请求频繁",lockName);
            throw new RuntimeException(msg);
        }
    }
}
