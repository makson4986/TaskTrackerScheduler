package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.RawReportDto;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.makson.tasktrackerscheduler.exceptions.DatabaseException;
import org.makson.tasktrackerscheduler.exceptions.ReportGenerationException;
import org.makson.tasktrackerscheduler.models.ReportStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerationService {
    private final KafkaService kafkaService;
    private final ApiClientService apiService;
    private final ReportService reportService;
    private final ExceptionHandlerService exceptionHandlerService;

    public void generateReports(LocalDate period) {
        List<UserDto> users = getUnprocessedUsers(apiService.getAllUser(), period);
        prepareGeneration(users, period);
        users.forEach(u -> generateReport(u, period));
    }

    private void generateReport(UserDto user, LocalDate period) {
        List<UserTasksDto.TaskDto> tasks = apiService.getUserTasks(user.id());
        UserTasksDto userTasks = new UserTasksDto(user.email(), period.toString(), tasks);

        try {
            ReportDto dto = kafkaService.sendTasksForSummarization(userTasks);
            log.info("The report for user '{}' has been successfully generated", user.email());
            reportService.saveAsGenerated(dto, period);
            log.info("The report for user '{}' has been successfully saved", user.email());
        } catch (ReportGenerationException e) {
            reportService.saveAsFailed(user.email(), period);
            exceptionHandlerService.handleReportGenerationException(e, user);
        } catch (DatabaseException e) {
            exceptionHandlerService.handleDatabaseException(e, user);
        }
    }

    private void prepareGeneration(List<UserDto> users, LocalDate period) {
        List<RawReportDto> rawReports = users.stream()
                .map(u -> new RawReportDto(u.email(), period, ReportStatus.PREPARING))
                .toList();
        reportService.prepareReports(rawReports);
        log.info("Preparing reports for generation for {} users", users.size());
    }

    private List<UserDto> getUnprocessedUsers(List<UserDto> allUsers, LocalDate period) {
        Set<String> processedRecipients = reportService.getProcessedRecipients(period);
        return allUsers.stream()
                .filter(u -> !processedRecipients.contains(u.email()))
                .toList();
    }
}
