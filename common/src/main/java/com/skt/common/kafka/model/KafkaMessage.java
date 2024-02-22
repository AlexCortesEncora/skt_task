package com.skt.common.kafka.model;

import com.skt.management_app.model.KafkaAction;

public class KafkaMessage {
    private KafkaAction action;

    private Object payload;

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
