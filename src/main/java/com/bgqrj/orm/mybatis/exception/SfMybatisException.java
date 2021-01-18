package com.bgqrj.orm.mybatis.exception;

/**
 * 描述: 异常类的基类
 *
 * @author yangxin
 * 日期: 2020/9/17
 */
public class SfMybatisException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public SfMybatisException(String message) {
        super(message);
    }
    public SfMybatisException(String message, Exception e) {
        super(message,e);
    }
    public SfMybatisException(Exception e){
        super(e);
    }


    public SfMybatisException(){
        super();
    }

}
