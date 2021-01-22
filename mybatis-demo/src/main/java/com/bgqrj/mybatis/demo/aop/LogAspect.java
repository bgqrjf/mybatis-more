package com.bgqrj.mybatis.demo.aop;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 描述: 请求日志切面
 *
 * @author yangxin
 * 日期: 2020/9/15
 */

@Slf4j
@Aspect
@Component
public class LogAspect {


    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.sftcwl.mybatis.demo.controller.*.*(..))")
    public void webLog() {
        //Pointcut
    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint joinPoint
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            log.error("======================= Request is EMPTY =======================");
        } else {
            HttpServletRequest request = attributes.getRequest();
            request.getContentType();
            // 打印请求相关参数
            log.info("======================= Request Start =======================");
            // 打印请求 url
            log.info("URL            : {}", request.getRequestURL().toString());
            // 打印 Http method
            log.info("HTTP Method    : {}", request.getMethod());
            // 打印调用 controller 的全路径以及执行方法
            log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            // 打印请求的 IP
            log.info("IP             : {}", request.getRemoteAddr());
            log.info("contentType    : {}", request.getContentType());
            // 打印请求入参
            if (Objects.nonNull(request.getContentType()) && !request.getContentType().contains("multipart/form-data")) {
                log.info("Request Params : {}", JSON.toJSONString(joinPoint.getArgs()));
            }

        }
    }

    /**
     * 在切点之后织入
     */
    @After("webLog()")
    public void doAfter() {
        log.info("======================= Request End =========================");
        // 每个请求之间空一行
        log.info("");
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint proceedingJoinPoint
     * @return result
     * @throws Throwable Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        log.info("Response Result  : {}", JSON.toJSONString(result));
        // 执行耗时
        log.info("执行时间          : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

}

