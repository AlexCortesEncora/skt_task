package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaMessage;

public interface SelectProductService {
    void processMessage(KafkaMessage kafkaMessage);
}
