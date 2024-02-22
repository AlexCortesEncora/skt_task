package com.skt.common.kafka.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.management_app.model.KafkaAction;

import java.io.IOException;
import java.util.List;

public class KafkaMessageServiceImpl implements KafkaMessageService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaMessage buildSelectRequest() {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setAction(KafkaAction.SELECT);
        return kafkaMessage;
    }

    public KafkaMessage buildSelectResponse(List<KafkaProduct> products) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setAction(KafkaAction.SELECT);
        kafkaMessage.setPayload(products);
        return kafkaMessage;
    }

    public KafkaMessage parsing(String message) {
        try {
            return objectMapper.readValue(message, KafkaMessage.class);
        } catch (JsonMappingException | JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
