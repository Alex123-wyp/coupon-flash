package org.yupeng.exception;

import lombok.Data;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Invalid argument
 * @author: yupeng
 **/
@Data
public class ArgumentError {
	
	private String argumentName;
	
	private String message;
}
