package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.TaskDto;
import org.makson.tasktrackerscheduler.dto.TaskStatus;
import org.makson.tasktrackerscheduler.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskReportService {
    private final KafkaService kafkaService;
    private final ReportTemplateService templateService;
    private final ApiClientService apiService;
    @Value("${spring.kafka.topic-name}")
    private String topicName;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendReports() {
        List<UserDto> users = apiService.getAllUser();

        for (UserDto user : users) {
//            createReport(user).ifPresent(report -> {
//                kafkaService.send(topicName, user.email(), report);
//                log.info("A task report for user {} has been sent.", user.email());
//            });
        }
    }
}
