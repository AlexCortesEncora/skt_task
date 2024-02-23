package com.skt.common.kafka.service;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public interface KafkaMessageService {
    KafkaMessage buildSelectRequest();

    KafkaMessage buildSelectResponse(UUID key, List<KafkaProduct> products);

    String parsingKafkaMessageToJson(KafkaMessage message);

    KafkaMessage parsingJsonToKafkaMessage(String json);

    List<KafkaProduct> parsingPayloadToKafkaProducts(Object payload);
}
