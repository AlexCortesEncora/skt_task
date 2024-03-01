package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaMessage;

public interface SaveProductService {
    void processMessage(KafkaMessage kafkaMessage);
}
