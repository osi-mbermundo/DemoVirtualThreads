package com.example.DemoVirtualThreads.service.client;

import com.example.DemoVirtualThreads.DemoVirtualThreadsApplication;
import com.example.DemoVirtualThreads.model.SensorData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = DemoVirtualThreadsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(locations = "classpath:application-test.yml")
@Execution(ExecutionMode.CONCURRENT)
class WebfluxWebClientTest {
    private static final Logger logger = LoggerFactory.getLogger(WebfluxWebClientTest.class);


    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${provider.service.rx.url}")
    private String providerUrl;
    public static MockWebServer mockBackEnd;

    private WebfluxWebClient webfluxWebClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s/producer/api/sensors/webflux",
                mockBackEnd.getPort());
        webfluxWebClient = new WebfluxWebClient(baseUrl, webClientBuilder);
    }

    @Test
    void verify_springboot_property() throws JsonProcessingException, ExecutionException, InterruptedException {
        mockServerClientCall();

        var result = webfluxWebClient.fetchSingleSensorData2().get();
        logger.info("Result:" + result);
    }

    private void mockServerClientCall() throws JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(getMockClientResponse()))
                .addHeader("Content-Type", "application/json"));
    }

    private String getMockClientResponse() throws JsonProcessingException {
        SensorData sensor = new SensorData("sensor-single", 24.2, 60.0, LocalDateTime.now());
        return objectMapper.writeValueAsString(sensor);
    }

}
