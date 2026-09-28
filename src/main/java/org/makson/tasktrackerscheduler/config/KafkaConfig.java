package org.makson.tasktrackerscheduler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.dto.UserTasksDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topic-name}")
    private String topicName;
    @Value("${spring.kafka.topics.summarization-requests}")
    private String summarizationRequestsTopic;
    @Value("${spring.kafka.topics.summarization-responses}")
    private String summarizationResponsesTopic;

    @Bean
    public NewTopic reportTopic() {
        return TopicBuilder
                .name(topicName)
                .build();
    }

    @Bean
    public NewTopic summarizationRequestsTopic() {
        return TopicBuilder
                .name(summarizationRequestsTopic)
                .build();
    }

    @Bean
    public NewTopic summarizationResponsesTopic() {
        return TopicBuilder
                .name(summarizationResponsesTopic)
                .build();
    }

    @Bean
    public ReplyingKafkaTemplate<String, UserTasksDto, ReportDto> replyingKafkaTemplate(
            ProducerFactory<String, UserTasksDto> producerFactory,
            ConcurrentMessageListenerContainer<String, ReportDto> repliesContainer
    ) {
        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, ReportDto> repliesContainer(ConcurrentKafkaListenerContainerFactory<String, ReportDto> containerFactory) {
        ConcurrentMessageListenerContainer<String, ReportDto> repliesContainer = containerFactory.createContainer(summarizationResponsesTopic);
        repliesContainer.getContainerProperties().setGroupId("summarizationServiceGroup");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }
}
