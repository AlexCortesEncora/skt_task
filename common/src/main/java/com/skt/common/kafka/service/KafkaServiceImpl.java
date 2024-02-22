package com.skt.common.kafka.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.common.kafka.model.KafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaServiceImpl.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topic;

    private final ObjectMapper objectMapper;

    public KafkaServiceImpl(KafkaTemplate<String, String> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void send(KafkaMessage message) {
        try {
            kafkaTemplate.send(this.topic, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOG.error("Error send", e);
        }
    }
}
