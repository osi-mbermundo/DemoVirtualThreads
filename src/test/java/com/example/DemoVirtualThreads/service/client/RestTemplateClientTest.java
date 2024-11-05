package com.example.DemoVirtualThreads.service.client;

import com.example.DemoVirtualThreads.DemoVirtualThreadsApplication;
import com.example.DemoVirtualThreads.model.SensorData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = DemoVirtualThreadsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(locations = "classpath:application-test.yml")
@Execution(ExecutionMode.CONCURRENT)
class RestTemplateClientTest {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateClientTest.class);


    @Autowired
    private RestTemplateClient restTemplateClient;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${provider.service.rx.url}")
    private String providerUrl;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        assertNotNull(mockServer);
    }


    @Test
    void verify_get_single_with_virtual_thread() throws ExecutionException, InterruptedException {
        final String url1 = providerUrl + "/single1";
        final String url2 = providerUrl + "/single2";
        mockServer.expect(ExpectedCount.times(2), requestTo(url1))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    addDelay(5000);
                    return withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(getMockClientResponse())
                            .createResponse(request);
                });
        mockServer.expect(ExpectedCount.times(2), requestTo(url2))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    addDelay(2000);
                    return withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(getMockClientResponse())
                            .createResponse(request);
                });

        CompletableFuture<ResponseEntity<String>> future1 = CompletableFuture.supplyAsync(() ->
                testRestTemplate.getForEntity("/api/sensors/virtual", String.class)
        );

        CompletableFuture<ResponseEntity<String>> future2 = CompletableFuture.supplyAsync(() ->
                testRestTemplate.getForEntity("/api/sensors/virtual", String.class)
        );

        CompletableFuture<ResponseEntity<String>> future3 = CompletableFuture.supplyAsync(() ->
                testRestTemplate.getForEntity("/api/sensors/logs", String.class)
        );

// Wait for both calls to complete
        ResponseEntity<String> response1 = future1.get();
        ResponseEntity<String> response2 = future2.get();
        ResponseEntity<String> response3 = future3.get();

    }

    @Test
    void verify_platform_threads_with_rest_client_call() {
        mockServerClientCall(10); // This is mocking of client call, and respond after 5secs.

        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
            for (int i = 1; i <= 10; i++) {
                String taskName = "Task" + i;
                executor.execute(() -> restTemplateClient.callEndpoint(taskName));
            }
        }
    }

    @Test
    void verify_virtual_threads_with_rest_client_call() {
        mockServerClientCall(10); // This is mocking of client call, and respond after 5secs.

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 10; i++) {
                String taskName = "Task" + i;
                executor.execute(() -> restTemplateClient.callEndpoint(taskName));
            }
        }
    }

    @Test
    void verify_springboot_property() {
        mockServerClientCall(1);

        restTemplateClient.callEndpoint("ThreadsVirtualEnabled");
    }

    private void mockServerClientCall(int expectedTimes) {
        final String url1 = providerUrl + "/single";
        mockServer.expect(ExpectedCount.times(expectedTimes), requestTo(url1))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    addDelay(5000);
                    return withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(getMockClientResponse())
                            .createResponse(request);
                });
    }

    private static void addDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMockClientResponse() throws JsonProcessingException {
        SensorData sensor = new SensorData("sensor-single", 24.2, 60.0, LocalDateTime.now());
        return objectMapper.writeValueAsString(sensor);
    }

}
