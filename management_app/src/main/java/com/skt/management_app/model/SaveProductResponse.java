package com.skt.management_app.model;

public class SaveProductResponse extends ProductResponse {
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
