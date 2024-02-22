package com.skt.management_app.consumer;

import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.management_app.core.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private ProductService productService;

    @KafkaListener(topics = "${kafka.topic.response.name}")
    public void listener(String message) {
        LOG.info("Message received {} ", message);
        productService.processMessage(kafkaMessageService.parsing(message));
    }
}
