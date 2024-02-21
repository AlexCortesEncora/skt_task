package com.skt.common.kafka.model;

import com.skt.common.model.Product;

import java.util.List;

public class KafkaMessageSelectResponse extends KafkaMessage {
    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
