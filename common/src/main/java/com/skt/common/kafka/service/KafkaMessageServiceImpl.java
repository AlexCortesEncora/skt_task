package com.skt.common.kafka.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.common.exception.input.InputDataException;
import com.skt.common.exception.input.MalformedDataException;
import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.util.SecurityEscape;
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

    @Override
    public KafkaMessage buildSelectRequest() {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(UUID.randomUUID());
        kafkaMessage.setAction(KafkaAction.SELECT);
        return kafkaMessage;
    }

    @Override
    public KafkaMessage buildSaveProductRequest(String name, String description, Float price) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(UUID.randomUUID());
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setPayload(new KafkaProduct(SecurityEscape.cleanIt(name), SecurityEscape.cleanIt(description), price));
        return kafkaMessage;

    }

    @Override
    public KafkaMessage buildSelectSuccessResponse(UUID key, List<KafkaProduct> products) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SELECT);
        kafkaMessage.setStatus(KafkaMessageStatus.SUCCESS);
        kafkaMessage.setPayload(products);
        return kafkaMessage;
    }

    @Override
    public KafkaMessage buildSaveSuccessResponse(UUID key, Integer id) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setStatus(KafkaMessageStatus.SUCCESS);
        kafkaMessage.setPayload(id);
        return kafkaMessage;
    }

    @Override
    public KafkaMessage buildErrorResponse(UUID key, KafkaAction action, String message) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(action);
        kafkaMessage.setStatus(KafkaMessageStatus.ERROR);
        kafkaMessage.setPayload(message);
        return kafkaMessage;
    }

    @Override
    public String parsingKafkaMessageToJson(KafkaMessage message) throws MalformedDataException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            LOG.error("Kafka Message can't be parsing to JSON: {}", ex.getMessage());
            throw new MalformedDataException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public KafkaMessage parsingJsonToKafkaMessage(String json) throws InputDataException {
        try {
            return objectMapper.readValue(json, KafkaMessage.class);
        } catch (JsonMappingException | JsonParseException ex) {
            LOG.error("JSON can't be parsing to KafkaMessage: {}", ex.getMessage());
            throw new MalformedDataException(ex.getMessage(), ex.getCause());
        } catch (IOException ex) {
            LOG.error("IO exception: {}", ex.getMessage());
            throw new InputDataException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public List<KafkaProduct> parsingPayloadToKafkaProducts(Object payload) {
        try {
            return objectMapper.convertValue(payload, new TypeReference<List<KafkaProduct>>() {
            });
        } catch (IllegalArgumentException ex) {
            LOG.error("Kafka Payload Message can't be parsing to Kafka Product list: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public KafkaProduct parsingPayloadToKafkaProduct(Object payload) {
        try {
            return objectMapper.convertValue(payload, KafkaProduct.class);
        } catch (IllegalArgumentException ex) {
            LOG.error("Payload can't be parsing to KafkaProduct: {}", ex.getMessage());
            throw new MalformedDataException(ex.getMessage(), ex.getCause());
        }
    }
}
