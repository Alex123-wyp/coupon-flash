package org.yupeng.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Current limiting configuration
 * @author: yupeng
 **/
@Data
//Define binding rules
@ConfigurationProperties(prefix = SeckillRateLimitConfigProperties.PREFIX)
public class SeckillRateLimitConfigProperties implements Serializable {
    
    public static final String PREFIX = "rate-limit";

    /**
     * Whether enable sliding window rate limit? (False by default)
     */
    private Boolean enableSlidingWindow = false;

    private Integer ipWindowMillis = 5000;
    
    private Integer ipMaxAttempts = 3;
    
    private Integer userWindowMillis = 60000;
    
    private Integer userMaxAttempts = 5;

    /**
     * IP white list, hit and pass directly
     */
    private Set<String> ipWhitelist = Collections.emptySet();

    /**
     * IP white list, hit and pass directly
     */
    private Set<Long> userWhitelist = Collections.emptySet();

    /**
     * Whether open penalty mechanism(false by default to avoid side effect)
     */
    private Boolean enablePenalty = false;

    /**
     * Being blocked time window. Unit: second.
     */
    private Integer violationWindowSeconds = 60;

    /**
     * IP block threshold, if hit and trigger block
     */
    private Integer ipBlockThreshold = 5;

    /**
     * User block threshold, if hit and trigger block
     */
    private Integer userBlockThreshold = 5;

    /**
     * IP block for 300 seconds
     */
    private Integer ipBlockTtlSeconds = 300;

    /**
     * User block for 300 seconds
     */
    private Integer userBlockTtlSeconds = 300;

    private EndpointLimit issue = new EndpointLimit();
    
    private EndpointLimit seckill = new EndpointLimit();

    @Data
    public static class EndpointLimit implements Serializable {

        /**
         * IP sliding window mill seconds,
         */
        private Integer ipWindowMillis;
        /**
         * IP maximum attempts
         */
        private Integer ipMaxAttempts;

        private Integer userWindowMillis;
        private Integer userMaxAttempts;

    }
}