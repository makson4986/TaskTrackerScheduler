package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ApiClientService apiService;
    private final ReportService reportService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendGenerationRequest() {
        List<UserDto> users = apiService.getAllUser();
        Set<String> processedRecipients = reportService.getProcessedRecipients();

        users.forEach(user -> {
            if (!processedRecipients.contains(user.email())) {
                reportService.generateReport(user);
            }
        });
    }

//    @Scheduled(cron = "0 0 6 * * *")
//    public void sendCompletedTask() {
//
//    }
}
