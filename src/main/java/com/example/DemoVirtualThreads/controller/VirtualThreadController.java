package com.example.DemoVirtualThreads.controller;

import com.example.DemoVirtualThreads.service.client.RestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensors")
public class VirtualThreadController {
    private static final Logger logger = LoggerFactory.getLogger(VirtualThreadController.class);

    private final RestTemplateClient client;

    public VirtualThreadController(RestTemplateClient client) {
        this.client = client;
    }

    @GetMapping(value = "/virtual", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<String> getSensorUpdates() {
        ResponseEntity<String> response1 = client.getSingleSensorUpdate(5000, "/single1");
        ResponseEntity<String> response2 = client.getSingleSensorUpdate(2000, "/single2");
        return ResponseEntity.ok("OK-getSensorUpdates");
    }


    @GetMapping(value = "/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<String> displayLogs() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            client.addLogs(String.valueOf(i)); // Log the current number
            Thread.sleep(1000); // Sleep for 1 second
        }
        return ResponseEntity.ok("OK-displayLogs");
    }

}
