package org.makson.tasktrackerscheduler.services;

import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.exceptions.DatabaseException;
import org.makson.tasktrackerscheduler.exceptions.ReportGenerationException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class ExceptionHandlerService {
    public void handleReportGenerationException(ReportGenerationException e, String user) {
        Throwable cause = e.getCause();

        switch (cause) {
            case TimeoutException _ -> log.warn("Report generation timeout for the user {}", user);
            case ExecutionException ex -> log.error("Kafka execution error for user {}: {}", user, ex.getMessage());

            case InterruptedException _ -> {
                Thread.currentThread().interrupt();
                log.warn("Interrupted for user {}", user);
            }
            default -> log.error("Unhandled Kafka exception for the user {}: {}", user, e.getStackTrace());
        }
    }

    public void handleDatabaseException(DatabaseException e, String user) {
        log.error("Database exception for user {}: {}, {}", user, e.getCause(), e.getMessage());
    }
}
