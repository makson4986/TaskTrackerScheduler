package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ReportGenerationService reportService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendGenerationRequest() {
        LocalDate period = LocalDate.now().minusDays(1);
        reportService.generateReports(period);
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sendCompletedTask() {
//        List<ReportDto> reports = reportService.getAllReport(LocalDate.now().minusDays(1));
//        reports.forEach(kafkaService::publishReportCreatedEvent);
    }
}
