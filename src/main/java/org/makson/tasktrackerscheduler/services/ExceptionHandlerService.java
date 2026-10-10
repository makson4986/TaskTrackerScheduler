package org.makson.tasktrackerscheduler.services;

import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.makson.tasktrackerscheduler.exceptions.DatabaseException;
import org.makson.tasktrackerscheduler.exceptions.ReportGenerationException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class ExceptionHandlerService {
    public void handleException(Exception e, UserDto user) {
        switch (e) {
            case DatabaseException ex -> handleDatabaseException(ex, user);
            case ReportGenerationException ex -> handleReportGenerationException(ex, user);
            default ->
                    log.error("Unhandled exception during report generation for the user {}: {}", user.email(), e.getStackTrace());
        }
    }

    private void handleReportGenerationException(ReportGenerationException e, UserDto user) {
        Throwable cause = e.getCause();

        switch (cause) {
            case TimeoutException _ -> log.warn("Report generation timeout for the user {}", user.email());
            case ExecutionException ex -> log.error("Kafka execution error for user {}: {}", user.email(), ex.getMessage());

            case InterruptedException _ -> {
                Thread.currentThread().interrupt();
                log.warn("Interrupted for user {}", user.email());
            }
            default -> log.error("Unhandled Kafka exception for the user {}: {}", user.email(), e.getStackTrace());
        }
    }

    private void handleDatabaseException(DatabaseException e, UserDto user) {
        log.error("Database exception for user {}: {}, {}", user.email(), e.getCause(), e.getMessage());
    }
}
