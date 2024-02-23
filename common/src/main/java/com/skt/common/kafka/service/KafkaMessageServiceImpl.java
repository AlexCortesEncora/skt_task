package com.skt.common.kafka.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.management_app.model.KafkaAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class KafkaMessageServiceImpl implements KafkaMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageServiceImpl.class);

    private final ObjectMapper objectMapper;

    public KafkaMessageServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KafkaMessage buildSelectRequest() {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(UUID.randomUUID());
        kafkaMessage.setAction(KafkaAction.SELECT);
        return kafkaMessage;
    }

    public KafkaMessage buildSelectResponse(UUID key, List<KafkaProduct> products) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SELECT);
        kafkaMessage.setPayload(products);
        return kafkaMessage;
    }

    public String parsingKafkaMessageToJson(KafkaMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            LOG.error("Kafka Message can't be parsing to JSON: {}", ex.getMessage());
            throw new RuntimeException();
        }
    }

    public KafkaMessage parsingJsonToKafkaMessage(String json) {
        try {
            return objectMapper.readValue(json, KafkaMessage.class);
        } catch (JsonMappingException | JsonParseException ex) {
            LOG.error("JSON can't be parsing to KafkaMessage: {}", ex.getMessage());
            throw new RuntimeException(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<KafkaProduct> parsingPayloadToKafkaProducts(Object payload) {
        try {
            return objectMapper.convertValue(payload, new TypeReference<List<KafkaProduct>>() {
            });
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
