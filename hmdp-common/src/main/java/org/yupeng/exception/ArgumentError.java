package org.yupeng.exception;

import lombok.Data;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 yupeng 微信，添加时备注 点评 来获取项目的完整资料
 * @description: 参数错误
 * @author: yupeng
 **/
@Data
public class ArgumentError {
	
	private String argumentName;
	
	private String message;
}
