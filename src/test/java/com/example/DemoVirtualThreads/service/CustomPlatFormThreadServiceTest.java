package com.example.DemoVirtualThreads.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class CustomPlatFormThreadServiceTest {

    @Autowired
    private CustomPlatFormThreadService customPlatFormThreadService;

    @Test
    void verify_getPlatformThread() {
        customPlatFormThreadService.getPlatformThread();
    }

    @Test
    void verify_getPlatformThreadWithExecutor() {
        customPlatFormThreadService.getPlatformThreadWithExecutor();
    }

    @Test
    void verify_getAsyncPlatformThread() {
        customPlatFormThreadService.getAsyncPlatformThread();
    }

    @Test
    void verify_getAsyncPlatformThreadWithExecutor() {
        customPlatFormThreadService.getAsyncPlatformThreadWithExecutor();
    }

    @Test
    void verify_getThreadOfPlatformStarted() {
        customPlatFormThreadService.getThreadOfPlatformStarted();
    }

    @Test
    void verify_getThreadOfPlatformUnstarted() {
        customPlatFormThreadService.getThreadOfPlatformUnstarted();
    }
}
