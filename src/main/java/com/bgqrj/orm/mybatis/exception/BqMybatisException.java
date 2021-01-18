package com.bgqrj.orm.mybatis.exception;

/**
 * 描述: 异常类的基类
 *
 * @author yangxin
 * 日期: 2020/9/17
 */
public class BqMybatisException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public BqMybatisException(String message) {
        super(message);
    }
    public BqMybatisException(String message, Exception e) {
        super(message,e);
    }
    public BqMybatisException(Exception e){
        super(e);
    }


    public BqMybatisException(){
        super();
    }

}
