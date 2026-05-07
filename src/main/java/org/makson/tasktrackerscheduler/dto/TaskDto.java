package org.makson.tasktrackerscheduler.dto;


import java.time.LocalDateTime;

public record TaskDto(
        int id,
        String title,
        String text,
        UserDto owner,
        TaskStatus status,
        LocalDateTime performedAt
) {
}