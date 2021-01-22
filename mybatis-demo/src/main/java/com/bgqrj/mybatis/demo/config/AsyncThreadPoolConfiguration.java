package com.bgqrj.mybatis.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述: 异步任务线程池配置
 *
 * @author yangxin
 * 日期: 2020/6/2
 */
@Slf4j
@Configuration
public class AsyncThreadPoolConfiguration {
    @Bean
    public Executor asyncServiceExecutor() {
        log.info("初始化异步线程池");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(4);
        //配置最大线程数
        executor.setMaxPoolSize(4);
        //配置队列大小
        executor.setQueueCapacity(8);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("simple-async-");
        // 设置拒绝策略：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
