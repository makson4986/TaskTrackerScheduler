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
            createReport(user).ifPresent(report -> {
                kafkaService.send(topicName, user.email(), report);
                log.info("A task report for user {} has been sent.", user.email());
            });
        }
    }

    private Optional<ReportDto> createReport(UserDto user) {
        final String reportTitle = "Отчет о задач за %s";

        return generateContent(user)
                .map(content -> new ReportDto(
                        user.email(),
                        reportTitle.formatted(LocalDate.now().toString()),
                        content
                ));
    }

    private Optional<String> generateContent(UserDto user) {
        List<TaskDto> uncompletedTasks = apiService.getUserTasks(user, TaskStatus.NOT_DONE);
        List<TaskDto> completedTasks = apiService.getUserTasks(user, TaskStatus.DONE);

        if (!hasTasks(completedTasks, uncompletedTasks)) {
            return Optional.empty();
        }

        String template = defineTemplate(uncompletedTasks.size(), completedTasks.size());
        String filledTemplate = templateService.fillTemplate(template, bindValueToPlaceholders(completedTasks, uncompletedTasks));
        return Optional.of(filledTemplate);
    }

    private String defineTemplate(int numberUncompletedTasks, int numberCompletedTasks) {
        if (numberUncompletedTasks > 0 && numberCompletedTasks > 0) {
            return templateService.getAllTaskTemplate();
        } else if (numberUncompletedTasks > 0) {
            return templateService.getUnCompletedTaskTemplate();
        } else {
            return templateService.getCompletedTaskTemplate();
        }
    }

    private String getTaskTitles(List<TaskDto> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> "%d. %s".formatted(i + 1, tasks.get(i).title()))
                .collect(Collectors.joining("\n"));
    }

    private boolean hasTasks(List<TaskDto> completedTasks, List<TaskDto> uncompletedTasks) {
        return !completedTasks.isEmpty() || !uncompletedTasks.isEmpty();
    }

    private Map<TemplatePlaceholder, String> bindValueToPlaceholders(List<TaskDto> completedTasks, List<TaskDto> uncompletedTasks) {
        return Map.of(
                TemplatePlaceholder.NUMBER_COMPLETED_TASK, String.valueOf(completedTasks.size()),
                TemplatePlaceholder.NUMBER_UNCOMPLETED_TASK, String.valueOf(uncompletedTasks.size()),
                TemplatePlaceholder.LIST_COMPLETED_TASK, getTaskTitles(completedTasks),
                TemplatePlaceholder.LIST_UNCOMPLETED_TASK, getTaskTitles(uncompletedTasks)
        );
    }
}
