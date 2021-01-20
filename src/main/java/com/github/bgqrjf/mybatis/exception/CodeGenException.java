package com.github.bgqrjf.mybatis.exception;

/**
 * 描述: mbg异常类
 *
 * @author yangxin
 * 日期: 2020/9/17
 */
public class CodeGenException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public CodeGenException(String message) {
        super(message);
    }
    public CodeGenException(String message, Exception e) {
        super(message,e);
    }
    public CodeGenException(Exception e){
        super(e);
    }


    public CodeGenException(){
        super();
    }

}
