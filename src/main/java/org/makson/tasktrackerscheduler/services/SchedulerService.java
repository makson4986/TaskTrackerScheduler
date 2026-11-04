package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.models.ReportStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ReportGenerationService reportGenerationService;
    private final ReportService reportService;
    private final KafkaService kafkaService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendGenerationRequest() {
        LocalDate period = LocalDate.now().minusDays(1);
        reportGenerationService.generateReports(period);
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sendCompletedTask() {
        LocalDate period = LocalDate.now().minusDays(1);
        List<ReportDto> reports = reportService.getAllReport(period, ReportStatus.GENERATED);
        reports.forEach(kafkaService::publishReportCreatedEvent);
    }
}
