package org.makson.tasktrackerscheduler.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.makson.tasktrackerscheduler.services.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ReportGenerationServiceTest {
    @Mock
    private KafkaService kafkaService;
    @Mock
    private ApiClientService apiService;
    @Mock
    private ReportService reportService;
    @InjectMocks
    private ReportGenerationService reportGenerationService;

    @Test
    @DisplayName("A report is not sent for generation if a report has already been generated for this user")
    void shouldNotSendToLlmIfUsersAlreadyProcessed() {
        LocalDate period = LocalDate.now();

        Mockito.doReturn(getTestUserTaskDto())
                .when(apiService)
                .getTasksForReport(period);
        Mockito.doReturn(Set.of("user1@test.test", "user3@test.test"))
                .when(reportService)
                .getProcessedRecipients(period);

        reportGenerationService.generateReports(LocalDate.now());

        ArgumentCaptor<UserTasksDto> captor = ArgumentCaptor.forClass(UserTasksDto.class);
        Mockito.verify(kafkaService, Mockito.times(1)).sendTasksForSummarization(captor.capture());
        UserTasksDto userTasks = captor.getValue();
        Assertions.assertEquals("user2@test.test", userTasks.userEmail());
    }

    List<UserTasksDto> getTestUserTaskDto() {
        return List.of(
                new UserTasksDto("user1@test.test", LocalDate.now(), Collections.emptyList()),
                new UserTasksDto("user2@test.test", LocalDate.now(), Collections.emptyList()),
                new UserTasksDto("user3@test.test", LocalDate.now(), Collections.emptyList())
        );
    }
}