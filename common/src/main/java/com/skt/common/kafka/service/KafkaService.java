package com.skt.common.kafka.service;

import com.skt.common.kafka.model.KafkaMessage;

public interface KafkaService {
    void send(KafkaMessage message);

    KafkaMessage parsing(String message);
}
