package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.SaveProductResponse;
import com.skt.management_app.model.SelectProductResponse;

import java.util.List;

public interface ProductResponseBuilder {
    SelectProductResponse buildSelectProductsSuccessResponse(List<Product> products);

    SelectProductResponse buildSelectProductsErrorResponse(String message);

    SaveProductResponse buildSaveProductSuccessResponse(Product product);

    SaveProductResponse buildSaveProductErrorResponse(String message);
}
