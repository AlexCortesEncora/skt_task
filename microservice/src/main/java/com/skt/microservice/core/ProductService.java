package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaMessage;

public interface ProductService {
    void processMessage(KafkaMessage message);
}
