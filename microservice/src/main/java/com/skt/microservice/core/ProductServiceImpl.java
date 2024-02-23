package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.microservice.persistence.ProductRepository;
import com.skt.microservice.persistence.entity.ProductEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private KafkaService kafkaService;

    @Transactional
    @Override
    public void processMessage(KafkaMessage message) {
        switch (message.getAction()) {
            case SELECT:
                processSelectionAction(message);
                break;
            default:
                LOG.error("Unsupported Action: " + message.getAction());
                break;
        }
    }

    private void processSelectionAction(KafkaMessage message) {
        List<ProductEntity> productEntities = productRepository.findAllProducts();
        List<KafkaProduct> products = productEntities
                .stream()
                .map(this::kafkaProduct)
                .collect(Collectors.toList());
        kafkaService.send(kafkaMessageService.buildSelectResponse(message.getKey(), products));
    }

    private KafkaProduct kafkaProduct(ProductEntity productEntity) {
        return new KafkaProduct(productEntity.getName(), productEntity.getDescription(), productEntity.getPrice());
    }
}
