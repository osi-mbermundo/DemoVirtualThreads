package com.example.DemoVirtualThreads.service;

import com.example.DemoVirtualThreads.util.ThreadLoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CustomPlatFormThreadService {
    private static final Logger logger = LoggerFactory.getLogger(CustomPlatFormThreadService.class);

    public void getPlatformThread() {
        Thread thread = new Thread(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getPlatformThread"));
        thread.start();
    }

    public void getPlatformThreadWithExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> ThreadLoggerUtil.logThreadInfo(logger, "getPlatformThreadWithExecutor"));

        executor.shutdown();
    }

    public void getAsyncPlatformThread() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            ThreadLoggerUtil.logThreadInfo(logger, "getAsyncPlatformThread");
            return "Success";
        });

        future.join();
    }

    public void getAsyncPlatformThreadWithExecutor() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            ThreadLoggerUtil.logThreadInfo(logger, "getAsyncPlatformThreadWithExecutor");
            return "Success";
        }, executorService);

        future.join();
        executorService.shutdown();
    }

    public void getThreadOfPlatformStarted() {
        Thread.ofPlatform().start(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getThreadOfPlatform (started)"));
    }

    public void getThreadOfPlatformUnstarted() {
        Thread thread = Thread.ofPlatform().unstarted(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getThreadOfPlatform (unstarted)"));
        thread.start();
    }


    public static void main(String[] args) {
        CustomPlatFormThreadService service = new CustomPlatFormThreadService();
        service.getPlatformThread();
        service.getPlatformThreadWithExecutor();
        service.getAsyncPlatformThread();
        service.getAsyncPlatformThreadWithExecutor();
        service.getThreadOfPlatformStarted();
        service.getThreadOfPlatformUnstarted();
    }
}
