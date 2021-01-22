package com.bgqrj.mybatis.demo.aop.handler;


import com.bgqrj.mybatis.demo.util.StringUtils;
import com.bgqrj.mybatis.demo.vo.base.ErrorInfo;
import com.bgqrj.mybatis.demo.enums.ResponseCode;
import com.bgqrj.mybatis.demo.exception.CustomException;
import com.bgqrj.mybatis.demo.util.ResultUtils;
import com.bgqrj.mybatis.demo.vo.base.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Set;

import static org.springframework.http.HttpStatus.OK;

/**
 * 描述: 全局异常处理
 *
 * @author yangxin
 * 日期: 2020/4/26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常,防止堆栈信息暴露服务器细节
     *
     * @param ex service层发生的异常
     * @return UNEXPECTED
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleRootException(Exception ex) {
        log.error("Unknown Error !", ex);
        return ResultUtils.error(new ErrorInfo(ResponseCode.UNEXPECTED.getCode(), ex.getMessage()),null);
    }

    /**
     * 缺少请求的必传的参数的异常
     *
     * @param ex 未处理的参数异常
     * @return MISSING_PARAM_EXCEPTION
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleMissingParamsException(MissingServletRequestParameterException ex) {
        log.error("Missing Servlet Request Parameter Exception  !", ex);
        return ResultUtils.error(new ErrorInfo(ResponseCode.MISSING_PARAM.getCode(), ResponseCode.MISSING_PARAM.getDesc()),null);
    }


    /**
     * 处理违反约束的异常
     *
     * @param exception 未处理的异常
     * @return MISSING_PARAM_EXCEPTION
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("Constraint Violation Exception ! ", exception);
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            stringBuilder.append(StringUtils.BLANK_STR)
                    .append(iterator.next().getMessage()).append(StringUtils.REG_EN_SEMICOLON);
        }
        return ResultUtils.error(new ErrorInfo(ResponseCode.MISSING_PARAM.getCode(), stringBuilder.toString()),null);
    }

    /**
     * 参数无效，如JSON请求参数违反约束
     *
     * @param exception 未处理的异常
     * @return METHOD_ARGUMENT_NOT_VALID
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("Method Argument Not Valid Exception ! ", exception);
        StringBuilder stringBuffer = new StringBuilder();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            stringBuffer.append(error.getDefaultMessage()).append(StringUtils.REG_EN_SEMICOLON);
        }
        return ResultUtils.error(new ErrorInfo(ResponseCode.METHOD_ARGUMENT_NOT_VALID.getCode(), stringBuffer.toString()),null);
    }

    /**
     * 绑定失败，如表单对象参数违反约束
     *
     * @param exception 未处理的异常
     * @return METHOD_ARGUMENT_NOT_VALID
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleBindException(BindException exception) {
        log.error("Bind Exception Occurred ! ", exception);
        StringBuilder stringBuffer = new StringBuilder();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            stringBuffer.append(error.getDefaultMessage()).append(";");
        }
        return ResultUtils.error(new ErrorInfo(ResponseCode.METHOD_ARGUMENT_NOT_VALID.getCode(), stringBuffer.toString()),null);
    }


    /**
     * 参数类型不匹配 未处理的异常
     *
     * @param exception 未处理的异常
     * @return METHOD_ARGUMENT_NOT_VALID
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleTypeMismatchException(TypeMismatchException exception) {
        log.error("Type Miss Match Exception!", exception);
        return ResultUtils.error(new ErrorInfo(ResponseCode.METHOD_ARGUMENT_NOT_VALID.getCode(), ResponseCode.METHOD_ARGUMENT_NOT_VALID.getDesc()),null);
    }

    /**
     * http请求超时异常
     *
     * @param exception 未处理的异常
     * @return SOCKET_TIME_OUT
     */
    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleSocketTimeoutException(SocketTimeoutException exception) {
        log.error("Socket Timeout Exception!", exception);
        return ResultUtils.error(new ErrorInfo(ResponseCode.SOCKET_TIMEOUT.getCode(), ResponseCode.SOCKET_TIMEOUT.getDesc()),null);
    }

    /**
     * 工程自定义异常
     */
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(OK)
    public ResultVO<Object> handleCommonException(CustomException customException) {
        log.error("CustomException occured ! ", customException);
        String errMsg = customException.getCode().getDesc();
        if (StringUtils.isNotEmpty(customException.getMessage())) {
            errMsg = customException.getMessage();
        }
        return ResultUtils.error(new ErrorInfo(customException.getCode().getCode(), errMsg),null);
    }

}
