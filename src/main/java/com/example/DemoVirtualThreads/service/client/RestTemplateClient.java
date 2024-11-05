package com.example.DemoVirtualThreads.service.client;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RestTemplateClient {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateClient.class);

    private final RestTemplate restTemplate;
    private final String providerUrl;

    private final String SINGLE_ENDPOINT;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public RestTemplateClient(
            @Value("${provider.service.rx.url}") String providerUrl,
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.providerUrl = providerUrl;
        this.SINGLE_ENDPOINT = this.providerUrl + "/single";
    }

    public ResponseEntity<String> getSingleSensorUpdate(int millis, String endpoint) {
        final String url = providerUrl + endpoint;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            return executor.submit(() -> {
                        Thread.currentThread().setName("DummyThreadName-" + Thread.currentThread().threadId());

                        // Log before sleeping
                        logger.info("Thread Name: " + Thread.currentThread().getName() + " is blocking (sleeping) for " + millis + " ms.");
                        Thread.sleep(millis); // sleep for specified milliseconds
                        logger.info("Thread Name: " + Thread.currentThread().getName() + " is resuming after sleeping.");

                        logger.info("Thread ID: " + Thread.currentThread().threadId());
                        logger.info("Is Virtual Thread: " + Thread.currentThread().isVirtual());
                        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                        logger.info("Thread Name: " + Thread.currentThread().getName() + " has fetched the data.");
                        return response;
                    })
                    .get();  // Wait for the result
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while fetching data from: " + url, e);
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ResponseEntity<String>> getSingleSensorUpdateAsync() {
        final String url = providerUrl + "/single";
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.currentThread().setName("VirtualThread-" + Thread.currentThread().threadId());
                logger.info("Thread Name: " + Thread.currentThread().getName() + " is fetching data from: " + url);
                Thread.sleep(2000); // simulate delay
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                logger.info("Thread Name: " + Thread.currentThread().getName() + " has fetched the data.");
                return response;
            } catch (InterruptedException e) {
                logger.error("Error while fetching data", e);
                throw new RuntimeException(e);
            }
        }, Executors.newVirtualThreadPerTaskExecutor()); // Use virtual threads for async tasks
    }


    public void addLogs(String logMsg) {
        logger.info(logMsg);
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
    }

    public void callEndpoint(String taskName) {
        logger.info(String.format("%s %s, Is Virtual: %s",
                Thread.currentThread() + " executing ", taskName, Thread.currentThread().isVirtual()));
        restTemplate.getForEntity(SINGLE_ENDPOINT, String.class);
        logger.info(Thread.currentThread() + " completed " + taskName);
    }
}
