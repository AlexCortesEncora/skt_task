package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.microservice.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void processMessage(KafkaMessage message) {
        switch (message.getAction()) {
            case SELECT:
                processSelectionAction();
                break;
            default:
                LOG.error("Unsupported Action: " + message.getAction());
                break;
        }
    }

    private void processSelectionAction() {
        productRepository.getAll();
    }
}
