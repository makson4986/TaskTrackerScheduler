package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
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

    public List<UserTasksDto.TaskDto> getUserTasks(int userId) {
        return webClient
                .get()
                .uri("/api/reports/users/{userId}/tasks", userId)
                .retrieve()
                .bodyToFlux(UserTasksDto.TaskDto.class)
                .collectList()
                .block();
    }
}
