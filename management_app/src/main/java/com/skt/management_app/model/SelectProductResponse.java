package com.skt.management_app.model;

import java.util.List;
import java.util.Objects;

public class SelectProductResponse extends ProductResponse {

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectProductResponse)) return false;
        SelectProductResponse that = (SelectProductResponse) o;
        return Objects.equals(getProducts(), that.getProducts()) && getStatus() == that.getStatus()
                && Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getMessage(), getProducts());
    }
}
