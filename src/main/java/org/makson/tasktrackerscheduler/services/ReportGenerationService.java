package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
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

    public void generateReports() {
        final LocalDate period = LocalDate.now().minusDays(1);
        List<UserDto> users = apiService.getAllUser();
        Set<String> processedRecipients = reportService.getProcessedRecipients();

        users.forEach(user -> {
            if (!processedRecipients.contains(user.email())) {
                generateReport(user, period);
            }
        });
    }

    private void generateReport(UserDto user, LocalDate period) {
        List<UserTasksDto.TaskDto> tasks = apiService.getUserTasks(user.id());
        UserTasksDto userTasks = new UserTasksDto(user.email(), period.toString(), tasks);

        try {
            ReportDto dto = kafkaService.sendTasksForSummarization(userTasks);
            log.info("The report for user '{}' has been successfully generated", user.email());
            reportService.save(dto, period);
            log.info("The report for user '{}' has been successfully saved", user.email());
        } catch (RuntimeException e) {
            exceptionHandlerService.handleException(e, user);
        }
    }
}
