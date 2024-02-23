package com.skt.common.kafka.service;

import com.skt.common.kafka.model.KafkaMessage;

import java.util.Optional;
import java.util.UUID;

public interface KafkaService {
    void send(KafkaMessage message);

    Optional<KafkaMessage> receiveMessage(UUID key);
}
