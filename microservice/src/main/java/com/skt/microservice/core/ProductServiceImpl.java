package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(SelectProductServiceImpl.class);

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private SelectProductService selectProductService;

    @Override
    public void processMessage(KafkaMessage message) {
        switch (message.getAction()) {
            case SELECT:
                selectProductService.processMessage(message);
                break;
            default:
                LOG.error("Unsupported Action: {}", message.getAction());
                kafkaService.send(kafkaMessageService.buildErrorResponse(message.getKey(), message.getAction(),
                        "Unsupported Action"));
                break;
        }
    }
}
