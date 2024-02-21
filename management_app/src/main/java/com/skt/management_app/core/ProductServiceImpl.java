package com.skt.management_app.core;

import com.skt.common.kafka.service.KafkaMessageBuilder;
import com.skt.common.kafka.service.KafkaService;
import com.skt.common.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private KafkaService kafkaService;

    @Override
    public List<Product> getProducts() {
        kafkaService.send(KafkaMessageBuilder.buildSelectRequest());

        List<Product> products = new ArrayList<>();
        products.add(build("table", "garden", 99.99f));
        products.add(build("chair", "garden", 9.9f));
        products.add(build("test", "garden", -100.99f));
        return products;
    }

    private Product build(String name, String description, float price) {
        return new Product(name, description, price);
    }
}
