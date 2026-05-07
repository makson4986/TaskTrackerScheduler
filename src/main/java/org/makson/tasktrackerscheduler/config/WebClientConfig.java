package org.makson.tasktrackerscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final String taskServiceUrl;

    public WebClientConfig(@Value("${task-service-url}") String taskServiceUrl) {
        this.taskServiceUrl = taskServiceUrl;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(taskServiceUrl)
                .build();
    }
}
