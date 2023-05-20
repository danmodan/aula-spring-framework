package com.danmodan.aulaspringframework.asynctaskexecution.config;

import com.danmodan.aulaspringframework.utilities.Util;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    // default SimpleAsyncTaskExecutor.setConcurrencyLimit(SimpleAsyncTaskExecutor.UNBOUNDED_CONCURRENCY);
    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("overrideGetAsyncExecutor-");

        // default ThreadPoolExecutor.AbortPolicy()
        //         ThreadPoolExecutor.DiscardPolicy()
        //         ThreadPoolExecutor.DiscardOldestPolicy()
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // executor.afterPropertiesSet();
        return executor;
    }

    // default SimpleAsyncUncaughtExceptionHandler
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> 
            Util.log(
                String.format(
                    "❌ %s (%s) - [ERROR] %s", 
                    method, 
                    Arrays.toString(params), 
                    ex.getMessage()
                )
            );
    }

    @Bean("specificExecutor")
    public Executor specificExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix("specificExecutor-");
        return taskExecutor;
    }
}
