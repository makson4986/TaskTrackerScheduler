package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.makson.tasktrackerscheduler.exceptions.ReportGenerationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {
    @Value("${spring.kafka.topics.summarization-requests}")
    private String summarizationRequestsTopic;
    @Value("${spring.kafka.topics.email-sending-tasks}")
    private String emailSendingTasksTopic;
    private final ReplyingKafkaTemplate<String, UserTasksDto, ReportDto> replyingKafkaTemplate;
    private final KafkaTemplate<String, ReportDto> kafkaTemplate;

    public ReportDto sendTasksForSummarization(UserTasksDto tasks) {
        ProducerRecord<String, UserTasksDto> record = new ProducerRecord<>(summarizationRequestsTopic, tasks.userEmail(), tasks);
        RequestReplyFuture<String, UserTasksDto, ReportDto> replyFuture = replyingKafkaTemplate.sendAndReceive(record, Duration.ofMinutes(2));
        log.info("Tasks for user '{}' for the period '{}' have been sent for summarization", tasks.userEmail(), tasks.period());

        try {
            return replyFuture.get(2, TimeUnit.MINUTES).value();
        } catch (Exception e) {
            throw new ReportGenerationException(e);
        }
    }

    public void publishReportCreatedEvent(ReportDto report) {
        kafkaTemplate.send(emailSendingTasksTopic, report.recipientEmail(), report);
    }
}
