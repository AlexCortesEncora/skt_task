package com.skt.common.kafka.service;

import com.skt.common.exception.input.InputDataException;
import com.skt.common.exception.input.MalformedDataException;
import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;

import java.util.List;
import java.util.UUID;

public interface KafkaMessageService {
    KafkaMessage buildSelectRequest();

    KafkaMessage buildSaveProductRequest(String name, String description, Float price);

    KafkaMessage buildSelectSuccessResponse(UUID key, List<KafkaProduct> products);

    KafkaMessage buildSaveSuccessResponse(UUID key, Integer id);

    KafkaMessage buildErrorResponse(UUID key, KafkaAction action, String message);

    String parsingKafkaMessageToJson(KafkaMessage message) throws MalformedDataException;

    KafkaMessage parsingJsonToKafkaMessage(String json) throws InputDataException;

    List<KafkaProduct> parsingPayloadToKafkaProducts(Object payload);

    KafkaProduct parsingPayloadToKafkaProduct(Object payload);
}
