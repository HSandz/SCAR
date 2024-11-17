package com.scar.lms.config;

import com.scar.lms.exception.handler.CustomAsyncExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AsyncConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomAsyncExceptionHandler customAsyncExceptionHandler;

    @Test
    public void asyncExecutorBeanShouldBeCreated() {
        ThreadPoolTaskExecutor executor = applicationContext.getBean(ThreadPoolTaskExecutor.class);
        assertNotNull(executor, "ThreadPoolTaskExecutor bean should be created");
        assertTrue(executor.getThreadNamePrefix().startsWith("AsyncExecutor-"), "Thread name prefix should be 'AsyncExecutor-'");
    }

    @Test
    public void asyncUncaughtExceptionHandlerBeanShouldBeCreated() {
        AsyncUncaughtExceptionHandler exceptionHandler = applicationContext.getBean(AsyncUncaughtExceptionHandler.class);
        assertNotNull(exceptionHandler, "AsyncUncaughtExceptionHandler bean should be created");
        assertInstanceOf(CustomAsyncExceptionHandler.class, exceptionHandler, "Exception handler should be an instance of CustomAsyncExceptionHandler");
    }
}