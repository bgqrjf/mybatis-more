package com.bgqrj.mybatis.demo.vo.base;

import com.bgqrj.mybatis.demo.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 2019/9/4
 *
 * @author yx
 */
@Data
@AllArgsConstructor
public class ErrorInfo {

     /**
      * 错误编码
      */
     private int code;

     /**
      * 错误提示消息
      */
     private String message;


     public ErrorInfo(ResponseCode errorCode) {
          this.code = errorCode.getCode();
          this.message = errorCode.getDesc();
     }

}
