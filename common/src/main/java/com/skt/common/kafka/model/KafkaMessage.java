package com.skt.common.kafka.model;

import com.skt.management_app.model.KafkaAction;

public abstract class KafkaMessage {
    private KafkaAction action;

    public KafkaAction getAction() {
        return action;
    }

    public void setAction(KafkaAction action) {
        this.action = action;
    }
}
