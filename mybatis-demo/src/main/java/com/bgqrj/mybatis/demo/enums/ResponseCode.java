package com.bgqrj.mybatis.demo.enums;


import com.bgqrj.mybatis.demo.enums.base.BaseEnum;

/**
 * @author yangxin
 * 描述: 全局response状态码
 * 2020-04-24
 */
public enum ResponseCode implements BaseEnum {

    /**
     * 规定系统级别通用错误code  601 - 999
     * 业务code  按照业务 分割 ，如 业务a 区间 1000-1999；业务b 2000-2999
     */


    SUCCESS(0, "success!"),

    UNEXPECTED(601, "未知错误!"),
    MISSING_PARAM(602, "请求错误!缺少请求的必须参数!"),
    METHOD_ARGUMENT_NOT_VALID(603, "方法参数校验失败！请检查参数类型或必传参数!"),
    SOCKET_TIMEOUT(604, "Http请求超时!"),
    CONNECT_TIMEOUT(605, "Http连接超时!"),
    DB_RECORD_NOT_FONT(606, "未在数据库查询到目标数据"),
    FILE_PARSE_ERROR(607, "文件解析异常"),
    ;


    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return code + ": " + desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
