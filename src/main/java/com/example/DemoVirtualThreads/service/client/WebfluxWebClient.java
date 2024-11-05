package com.example.DemoVirtualThreads.service.client;

import com.example.DemoVirtualThreads.util.ThreadLoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.CompletableFuture;
@Service
public class WebfluxWebClient {
    private static final Logger logger = LoggerFactory.getLogger(WebfluxWebClient.class);

    private final WebClient webClient;

    public WebfluxWebClient(
            @Value("${provider.service.webflux.url}") String providerUrl,
            WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.baseUrl(providerUrl).build();
    }

    // Mono: 0 or 1
    public void fetchSingleSensorData() {
        logger.info("fetchSingleSensorData");
        webClient.get()
                .uri("/single")
                .httpRequest(request -> ThreadLoggerUtil.logThreadInfo(logger, "WebfluxWebClient"))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe(subscription -> ThreadLoggerUtil.logThreadInfo(logger, "WebfluxWebClient: subscribe"))
                .doOnNext(response -> logger.info("Response received for fetchSingleSensorData"))
                .subscribe();
    }

    public CompletableFuture<String> fetchSingleSensorData2() {
        logger.info("fetchSingleSensorData");
        return CompletableFuture.supplyAsync(() -> {
            // Log thread info
            ThreadLoggerUtil.logThreadInfo(logger, "WebfluxWebClient");

            // Blocking call, can be executed in a virtual thread
            return webClient.get()
                    .uri("/single")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Use block() to get the result
        });
    }
}
