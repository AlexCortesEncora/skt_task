package com.skt.common.kafka.model;

import com.skt.management_app.model.KafkaAction;

import java.util.UUID;

public class KafkaMessage {
    private UUID key;
    private KafkaAction action;
    private Object payload;

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public KafkaAction getAction() {
        return action;
    }

    public void setAction(KafkaAction action) {
        this.action = action;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
