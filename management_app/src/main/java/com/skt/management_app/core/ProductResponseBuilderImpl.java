package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.ResponseStatus;
import com.skt.management_app.model.SelectProductResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductResponseBuilderImpl implements ProductResponseBuilder {

    @Override
    public SelectProductResponse buildSelectProductsSuccessResponse(List<Product> products) {
        SelectProductResponse response = new SelectProductResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setProducts(products);
        return response;
    }

    @Override
    public SelectProductResponse buildSelectProductsErrorResponse(String message) {
        SelectProductResponse response = new SelectProductResponse();
        response.setStatus(ResponseStatus.ERROR);
        response.setProducts(Collections.emptyList());
        response.setMessage(message);
        return response;
    }
}
