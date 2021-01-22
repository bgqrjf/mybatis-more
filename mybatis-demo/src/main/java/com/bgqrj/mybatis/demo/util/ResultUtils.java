package com.bgqrj.mybatis.demo.util;

import com.bgqrj.mybatis.demo.enums.ResponseCode;
import com.bgqrj.mybatis.demo.vo.base.ErrorInfo;
import com.bgqrj.mybatis.demo.vo.base.ResultVO;

/**
 * 描述: response工具类(用一句话描述该文件做什么)
 *
 * @author yangxin
 * 日期: 2020/7/8
 */
public class ResultUtils {
    private ResultUtils() {

    }

    /**
     * 创建成功返回的Response
     *
     * @return Response实例
     */
    public static ResultVO<String> success() {
        return new ResultVO<>(ResponseCode.SUCCESS.getCode());
    }

    /**
     * 创建成功返回的Response
     *
     * @param data 返回的数据
     * @param <T>  返回的数据的泛型
     * @return Response实例
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(new ErrorInfo(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc()), data);
    }

    /**
     * 创建失败返回的Response
     *
     * @param errorInfo 错误提示信息
     * @param data      返回的数据
     * @param <T>       返回的数据的泛型
     * @return Response实例
     */
    public static <T> ResultVO<T> error(ErrorInfo errorInfo, T data) {
        return new ResultVO<>(errorInfo, data);
    }

    /**
     * 创建失败返回的Response
     *
     * @param errorInfo 错误提示信息
     * @return Response实例
     */
    public static ResultVO<String> error(ErrorInfo errorInfo) {
        return new ResultVO<>(errorInfo);
    }


}
