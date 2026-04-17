package org.yupeng.message;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Message wrapper
 * @author: yupeng
 **/
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor

//Serializable tell Spring Framework this class is serializable
public final class MessageExtend<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    @NonNull
    private T messageBody;

    //Key is important, events with same key goes to same partition
    private String key;

    //Header always carry some auxiliary information that helps process and trace
    private Map<String, String> headers;
    
    private String uuid = UUID.randomUUID().toString();
    
    private Date producerTime = DateTime.now();


    //Wrap the payLoad
    public static <T> MessageExtend<T> of(T body){
        return new MessageExtend<>(body);
    }
    
    public static <T> MessageExtend<T> of(T body, String key, Map<String, String> headers){
        MessageExtend<T> msg = new MessageExtend<>(body);
        msg.setKey(key);
        msg.setHeaders(headers);
        return msg;
    }
}
