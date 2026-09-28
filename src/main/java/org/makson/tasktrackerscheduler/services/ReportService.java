package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.makson.tasktrackerscheduler.mappers.ReportMapper;
import org.makson.tasktrackerscheduler.models.Report;
import org.makson.tasktrackerscheduler.repositories.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final KafkaService kafkaService;
    private final ApiClientService apiService;
    private final ReportMapper reportMapper;
    private final ReportRepository reportRepository;

    public void generateReport(UserDto user) {
        List<UserTasksDto.TaskDto> tasks = apiService.getUserTasks(user.id());
        UserTasksDto userTasks = new UserTasksDto(user.email(), LocalDateTime.now().toString(), tasks);

        try {
            ReportDto report = kafkaService.sendTasksForSummarization(userTasks);
            reportRepository.save(reportMapper.toModel(report));
            log.info("The report for user '{}' has been successfully generated and saved", user.email());
        } catch (TimeoutException e) {
            log.warn("The timeout for a response from the report generation service has expired");
        } catch (ExecutionException e) {
            log.error("Error within the summarization service: {}", e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Report generation was interrupted");
        }
    }

    public Set<String> getProcessedRecipients() {
        return reportRepository.findProcessedRecipientsByPeriod(LocalDate.now().minusDays(1));
    }

    public List<ReportDto> getAllReport(LocalDate period) {
        List<Report> reports = reportRepository.getAllByPeriod(period);
        return reportMapper.toListDto(reports);
    }
}
