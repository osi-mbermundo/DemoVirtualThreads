package com.example.DemoVirtualThreads.util;

import org.slf4j.Logger;

public class ThreadLoggerUtil {

    public static void logThreadInfo(Logger logger, String methodName) {
        long threadId = Thread.currentThread().threadId();
        String threadName = Thread.currentThread().getName();
        boolean isVirtual = Thread.currentThread().isVirtual();
        logger.info(String.format("%s - Thread ID: %d, Name: %s, Is Virtual: %s",
                methodName, threadId, threadName, isVirtual));
    }
}
