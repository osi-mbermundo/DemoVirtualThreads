package com.example.DemoVirtualThreads.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class CustomVirtualThreadServiceTest {

    @Autowired
    private CustomVirtualThreadService customVirtualThreadService;

    @Test
    void verify_getVirtualThread() {
        customVirtualThreadService.getVirtualThread();
    }

    @Test
    void verify_getVirtualThreadWithExecutor() {
        customVirtualThreadService.getVirtualThreadWithExecutor();
    }

    @Test
    void verify_getAsyncVirtualThreadWithExecutor() {
        customVirtualThreadService.getAsyncVirtualThreadWithExecutor();
    }

    @Test
    void verify_getThreadOfVirtualStarted() {
        customVirtualThreadService.getThreadOfVirtualStarted();
    }

    @Test
    void verify_getThreadOfVirtualStartVirtualThread() {
        customVirtualThreadService.getThreadOfVirtualStartVirtualThread();
    }

    @Test
    void verify_getVirtualThreadWithSpecificName() {
        customVirtualThreadService.getVirtualThreadWithSpecificName();
    }

    @Test
    void verify_getVirtualThreadWithPrefixName() {
        customVirtualThreadService.getVirtualThreadWithPrefixName();
    }

    @Test
    void getVirtualThreadWithExecutorAndName() {
        customVirtualThreadService.getVirtualThreadWithExecutorAndName();
    }
}
