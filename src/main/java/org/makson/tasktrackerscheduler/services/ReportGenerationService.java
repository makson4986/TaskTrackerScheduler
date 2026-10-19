package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.DraftReportDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
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
        List<UserTasksDto> userTasks = prepareGeneration(period);
        userTasks.forEach(t -> generateReport(t, period));
    }

    private void generateReport(UserTasksDto userTasks, LocalDate period) {
        String recipientEmail = userTasks.userEmail();

        try {
            String reportContent = kafkaService.sendTasksForSummarization(userTasks);
            log.info("The report for user '{}' has been successfully generated", recipientEmail);
            reportService.saveAsGenerated(recipientEmail, period, reportContent);
            log.info("The report for user '{}' has been successfully saved", recipientEmail);
        } catch (Exception e) {
            reportService.saveAsFailed(recipientEmail, period);
            exceptionHandlerService.handleException(e, recipientEmail);
        }
    }

    private List<UserTasksDto> prepareGeneration(LocalDate period) {
        List<UserTasksDto> unprocessedUsers = getUnprocessedUsers(period);

        List<DraftReportDto> rawReports = unprocessedUsers.stream()
                .map(t -> new DraftReportDto(t.userEmail(), period, ReportStatus.PREPARING))
                .toList();

        reportService.prepareReports(rawReports);
        log.info("Preparing reports for generation");
        return unprocessedUsers;
    }

    private List<UserTasksDto> getUnprocessedUsers(LocalDate period) {
        List<UserTasksDto> userTasks = apiService.getTasksForReport(period);
        Set<String> processedUsers = reportService.getProcessedRecipients(period);

        return userTasks.stream()
                .filter(t -> !processedUsers.contains(t.userEmail()))
                .toList();
    }
}
