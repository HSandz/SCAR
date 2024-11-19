package com.scar.lms.exception.handler;

import lombok.NonNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(CustomAsyncExceptionHandler.class.getName());

    @Override
    public void handleUncaughtException(@NonNull Throwable throwable, @NonNull Method method, Object @NonNull ... obj) {
        LOGGER.log(Level.SEVERE, "Exception message - " + throwable.getMessage());
        LOGGER.log(Level.SEVERE, "Method name - " + method.getName());
        for (Object param : obj) {
            LOGGER.log(Level.SEVERE, "Parameter value - " + param);
        }
    }
}