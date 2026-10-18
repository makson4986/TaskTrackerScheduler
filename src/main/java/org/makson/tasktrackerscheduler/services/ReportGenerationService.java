package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.RawReportDto;
import org.makson.tasktrackerscheduler.dto.ReportDto;
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
        String userEmail = userTasks.userEmail();

        try {
            ReportDto dto = kafkaService.sendTasksForSummarization(userTasks);
            log.info("The report for user '{}' has been successfully generated", userEmail);
            reportService.saveAsGenerated(dto, period);
            log.info("The report for user '{}' has been successfully saved", userEmail);
        } catch (Exception e) {
            reportService.saveAsFailed(userEmail, period);
            exceptionHandlerService.handleException(e, userEmail);
        }
    }

    public List<UserTasksDto> prepareGeneration(LocalDate period) {
        List<UserTasksDto> userTasks = apiService.getTasksForReport(period);
        Set<String> processedUsers = reportService.getProcessedRecipients(period);

        List<RawReportDto> rawReports = userTasks.stream()
                .filter(t -> !processedUsers.contains(t.userEmail()))
                .map(t -> new RawReportDto(t.userEmail(), period, ReportStatus.PREPARING))
                .toList();

        reportService.prepareReports(rawReports);
        log.info("Preparing reports for generation for {} users", userTasks.size());
        return userTasks;
    }
}
