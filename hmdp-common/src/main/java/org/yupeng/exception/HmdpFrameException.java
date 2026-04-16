package org.yupeng.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.yupeng.enums.BaseCode;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Business exception
 * @author: yupeng
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class HmdpFrameException extends BaseException {
	
	private Integer code;
	
	private String message;

	public HmdpFrameException() {
		super();
	}

	public HmdpFrameException(String message) {
		super(message);
	}
	
	public HmdpFrameException(Integer code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
	
	public HmdpFrameException(BaseCode baseCode) {
		super(baseCode.getMsg());
		this.code = baseCode.getCode();
		this.message = baseCode.getMsg();
	}

	public HmdpFrameException(Throwable cause) {
		super(cause);
	}

	public HmdpFrameException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}
}
