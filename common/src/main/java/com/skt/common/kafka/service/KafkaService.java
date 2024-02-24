package com.skt.common.kafka.service;

import com.skt.common.exception.external.InfrastructureException;
import com.skt.common.kafka.model.KafkaMessage;

import java.util.Optional;
import java.util.UUID;

public interface KafkaService {
    void send(KafkaMessage message)  throws InfrastructureException;

    Optional<KafkaMessage> receiveMessage(UUID key);
}
