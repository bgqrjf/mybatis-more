package com.bgqrj.mybatis.demo.exception;


import com.bgqrj.mybatis.demo.enums.ResponseCode;

import lombok.ToString;

/**
 * 自定义异常
 * @author yx
 */
@ToString
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1;

    private final ResponseCode code;

    public CustomException(ResponseCode code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(ResponseCode code) {
        super("");
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }

}
