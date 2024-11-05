package com.example.DemoVirtualThreads.service;

import com.example.DemoVirtualThreads.util.ThreadLoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Service
public class CustomVirtualThreadService {
    private static final Logger logger = LoggerFactory.getLogger(CustomVirtualThreadService.class);

    public void getVirtualThread() {
        Thread thread = Thread.ofVirtual().unstarted(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getVirtualThread"));
        thread.start();
    }

    public void getVirtualThreadWithExecutor() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        executor.submit(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getVirtualThreadWithExecutor"));

        executor.shutdown();
    }

    public void getAsyncVirtualThreadWithExecutor() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            ThreadLoggerUtil.logThreadInfo(logger, "getAsyncVirtualThreadWithExecutor");
            return "Success";
        }, executorService);

        future.join();
        executorService.shutdown();
    }

    public void getThreadOfVirtualStarted() {
        Thread.ofVirtual().start(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getThreadOfVirtual (started)"));
    }

    public void getThreadOfVirtualStartVirtualThread() {
        Thread.startVirtualThread(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getThreadOfVirtual (startVirtualThread)"));
    }

    public void getVirtualThreadWithSpecificName() {
        Thread thread = Thread.ofVirtual().name("custom-threadName").unstarted(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getVirtualThread (with specific name)"));
        thread.start();
    }

    public void getVirtualThreadWithPrefixName() {
        Thread thread = Thread.ofVirtual().name("with-prefix-threadName", 0).unstarted(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getVirtualThread (with prefix name)"));
        thread.start();
    }

    public void getVirtualThreadWithExecutorAndName() {
        ThreadFactory factory = Thread.ofVirtual().name("prefix-exec-", 0).factory();
        ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);

        executor.submit(
                () -> ThreadLoggerUtil.logThreadInfo(logger, "getVirtualThreadWithExecutorAndName"));

        executor.shutdown();
    }


    public static void main(String[] args) {
        CustomVirtualThreadService service = new CustomVirtualThreadService();
        service.getVirtualThread();
        service.getVirtualThreadWithExecutor();
        service.getAsyncVirtualThreadWithExecutor();
        service.getThreadOfVirtualStarted();
        service.getThreadOfVirtualStartVirtualThread();
    }
}
