package com.skt.common.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaMessage {
    private UUID key;

    private KafkaMessageStatus status;

    private KafkaAction action;
    private Object payload;

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public KafkaMessageStatus getStatus() {
        return status;
    }

    public void setStatus(KafkaMessageStatus status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "key=" + key +
                ", action=" + action +
                ", payload=" + payload +
                '}';
    }
}
