package com.skt.management_app.model;

import java.util.List;

public class SelectProductResponse extends ProductResponse {

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
