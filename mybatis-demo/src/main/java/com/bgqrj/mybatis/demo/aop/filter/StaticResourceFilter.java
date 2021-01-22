package com.bgqrj.mybatis.demo.aop.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 描述: 安全性url过滤器
 * order指定过滤器的执行顺序,值越大越靠后执行
 * @author yangxin
 * 日期: 2020/7/9
 */
/*
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "staticResourceFilter")
@Order(1)
public class StaticResourceFilter implements Filter {

    @Value("${environment}")
    private String environment;

    private static final String OPEN_ENV = "dev";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String uri = httpServletRequest.getRequestURI();
        if (OPEN_ENV.equals(environment)) {
            chain.doFilter(request, response);
        } else {
            String druidPath = "druid";
            String swaggerPath = "swagger";
            if (uri.contains(druidPath) || uri.contains(swaggerPath)) {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/error");
            }
            else {
                chain.doFilter(request, response);
            }
        }
    }
}
*/
