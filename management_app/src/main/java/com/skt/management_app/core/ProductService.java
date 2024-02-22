package com.skt.management_app.core;


import com.skt.common.kafka.model.KafkaMessage;
import com.skt.management_app.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> sendGetAllMessage();

    void processMessage(KafkaMessage message);
}
