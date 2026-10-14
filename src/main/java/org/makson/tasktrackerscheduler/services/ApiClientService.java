package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiClientService {
    private final WebClient webClient;

    public List<UserTasksDto> getTasksForReport(LocalDate period) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/reports/tasks/pending-generation")
                        .queryParam("period", period)
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserTasksDto>>() {
                })
                .block();
    }
}
