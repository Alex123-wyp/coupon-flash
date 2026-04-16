package org.yupeng.ratelimit.extension;

import lombok.Data;

import java.util.List;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Rate-limit execution context
 * @author: yupeng
 **/
@Data
public class RateLimitContext {

    private Long voucherId;
    private Long userId;
    private String clientIp;

    private List<String> keys;
    private boolean useSliding;

    private int ipWindowMillis;
    private int ipMaxAttempts;
    private int userWindowMillis;
    private int userMaxAttempts;

    private Integer result;

    public RateLimitContext() {}

    public RateLimitContext(Long voucherId, Long userId, String clientIp,
                            List<String> keys, boolean useSliding,
                            int ipWindowMillis, int ipMaxAttempts,
                            int userWindowMillis, int userMaxAttempts) {
        this.voucherId = voucherId;
        this.userId = userId;
        this.clientIp = clientIp;
        this.keys = keys;
        this.useSliding = useSliding;
        this.ipWindowMillis = ipWindowMillis;
        this.ipMaxAttempts = ipMaxAttempts;
        this.userWindowMillis = userWindowMillis;
        this.userMaxAttempts = userMaxAttempts;
    }
}