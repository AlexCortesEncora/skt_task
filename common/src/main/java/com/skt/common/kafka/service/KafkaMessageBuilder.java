package com.skt.common.kafka.service;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageSelectRequest;
import com.skt.management_app.model.KafkaAction;

public class KafkaMessageBuilder {
    public static KafkaMessage buildSelectRequest() {
        KafkaMessageSelectRequest kafkaMessage = new KafkaMessageSelectRequest();
        kafkaMessage.setAction(KafkaAction.SELECT);
        return kafkaMessage;
    }
}
