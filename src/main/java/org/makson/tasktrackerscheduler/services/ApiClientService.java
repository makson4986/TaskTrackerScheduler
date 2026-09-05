package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.TaskDto;
import org.makson.tasktrackerscheduler.dto.TaskStatus;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiClientService {
    private final WebClient webClient;

    public List<UserDto> getAllUser() {
        return webClient
                .get()
                .uri("/api/reports/users")
                .retrieve()
                .bodyToFlux(UserDto.class)
                .collectList()
                .block();
    }

    public List<TaskDto> getUserTasks(String userId, TaskStatus status) {
        return webClient
                .get()
                .uri("/api/reports/users/{userId}/tasks?status={statusName}", userId, status.name())
                .retrieve()
                .bodyToFlux(TaskDto.class)
                .collectList()
                .block();
    }
}
