package com.skt.management_app.core;

import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.management_app.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Override
    public List<Product> sendGetAllMessage() {
        try {
            List<Product> products = new ArrayList<>();
            KafkaMessage kafkaMessage = kafkaMessageService.buildSelectRequest();
            kafkaService.send(kafkaMessage);
            kafkaService.receiveMessage(kafkaMessage.getKey())
                    .ifPresent(message -> products.addAll(processSelectionAction(message)));
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<Product> processSelectionAction(KafkaMessage message) {
        return (kafkaMessageService.parsingPayloadToKafkaProducts(message.getPayload()))
                .stream()
                .map(this::product)
                .collect(Collectors.toList());
    }

    private Product product(KafkaProduct kafkaProduct) {
        return new Product(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice());
    }
}
