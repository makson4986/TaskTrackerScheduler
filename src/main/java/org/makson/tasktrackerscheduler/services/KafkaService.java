package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String key, Object message) {
        String json = objectMapper.writeValueAsString(message);
        kafkaTemplate.send(topic, key, json);
    }
}
