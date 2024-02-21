package com.skt.microservice.consumer;

import com.skt.common.kafka.service.KafkaService;
import com.skt.microservice.core.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private KafkaService kafkaService;

    @KafkaListener(topics = "${kafka.topic.request.name}")
    public void listener(String message) {
        LOG.info("Message received {} ", message);
        productService.processMessage(kafkaService.parsing(message));
    }
}
