package com.example.DemoVirtualThreads.model;

import java.time.LocalDateTime;

public record SensorData(
        String id,
        double temperature,
        double humidity,
        LocalDateTime timestamp) {
}
