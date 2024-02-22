package com.skt.common.kafka.service;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;

import java.util.List;

public interface KafkaMessageService {
    KafkaMessage buildSelectRequest();

    KafkaMessage buildSelectResponse(List<KafkaProduct> products);

    KafkaMessage parsing(String message);
}
