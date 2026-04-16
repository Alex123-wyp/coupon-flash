package org.yupeng.exception;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Base exception
 * @author: yupeng
 **/
public class BaseException extends RuntimeException{
	
	public BaseException() {
		
	}
	
	public BaseException(String message) {
		super(message);
	}
	
	public BaseException(Throwable cause) {
		super(cause);
	}
	
	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(Integer code, String message, Throwable cause) {
		super(message, cause);
	}
}
