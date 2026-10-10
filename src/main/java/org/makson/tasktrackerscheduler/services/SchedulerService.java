package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ReportGenerationService reportService;
    private final KafkaService kafkaService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendGenerationRequest() {
        reportService.generateReports();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sendCompletedTask() {
//        List<ReportDto> reports = reportService.getAllReport(LocalDate.now().minusDays(1));
//        reports.forEach(kafkaService::publishReportCreatedEvent);
    }
}
