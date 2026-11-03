package org.makson.tasktrackerscheduler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topics.report}")
    private String reportTopic;
    @Value("${spring.kafka.topics.summarization-requests}")
    private String summarizationRequestsTopic;
    @Value("${spring.kafka.topics.summarization-responses}")
    private String summarizationResponsesTopic;

    @Bean
    public NewTopic emailSendingTasksTopic() {
        return TopicBuilder
                .name(reportTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic summarizationRequestsTopic() {
        return TopicBuilder
                .name(summarizationRequestsTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic summarizationResponsesTopic() {
        return TopicBuilder
                .name(summarizationResponsesTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public ReplyingKafkaTemplate<String, UserTasksDto, String> replyingKafkaTemplate(
            ProducerFactory<String, UserTasksDto> producerFactory,
            ConcurrentMessageListenerContainer<String, String> repliesContainer
    ) {
        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer(ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer = containerFactory.createContainer(summarizationResponsesTopic);
        repliesContainer.getContainerProperties().setGroupId("summarizationServiceGroup");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public KafkaTemplate<String, ReportDto> kafkaTemplate(ProducerFactory<String, ReportDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
