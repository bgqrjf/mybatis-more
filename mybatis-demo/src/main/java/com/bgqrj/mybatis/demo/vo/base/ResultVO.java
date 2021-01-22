package com.bgqrj.mybatis.demo.vo.base;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

/**
 * 统一API响应结果封装
 * @author yx
 */
@ToString
public class ResultVO<T> implements Serializable {

    /**
     * 返回的状态码
     */
    @JsonProperty(index = 1)
    @Getter
    @Setter
    private Integer errno;

    /**
     * 返回的消息
     */
    @JsonProperty(index = 2,value = "errmsg")
    @Getter
    @Setter
    private String errMsg;

    /**
     * 返回的业务数据
     */
    @JsonProperty(index = 3)
    @Getter
    @Setter
    private T data;


    @JsonProperty(index = 4)
    @Getter
    @Setter
    private Long st;

    /**
     * 无参构造器
     */
    public ResultVO() {
    }

    /**
     * 构造器
     *
     * @param errno Response的Code
     */
    public ResultVO(int errno) {
        this.errno = errno;
        this.st = Calendar.getInstance(Locale.CHINA).getTimeInMillis();
    }

    /**
     * 私有构造器
     *
     * @param errorInfo 错误提示信息
     */
    public ResultVO(ErrorInfo errorInfo) {
        this.errno = errorInfo.getCode();
        this.errMsg = errorInfo.getMessage();
        this.st = Calendar.getInstance(Locale.CHINA).getTimeInMillis();
    }

    /**
     * 私有构造器
     *
     * @param errorInfo 错误提示信息
     * @param data      返回的数据
     */
    public ResultVO(ErrorInfo errorInfo, T data) {
        this.errno = errorInfo.getCode();
        this.errMsg = errorInfo.getMessage();
        this.data = data;
        this.st = Calendar.getInstance(Locale.CHINA).getTimeInMillis();
    }


}
