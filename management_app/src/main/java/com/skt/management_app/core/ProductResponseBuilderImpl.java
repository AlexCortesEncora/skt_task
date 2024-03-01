package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.ResponseStatus;
import com.skt.management_app.model.SaveProductResponse;
import com.skt.management_app.model.SelectProductResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductResponseBuilderImpl implements ProductResponseBuilder {

    public static final String SAVE_PROD_SUCC_RS = "The Product was save successfully";

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

    @Override
    public SaveProductResponse buildSaveProductSuccessResponse(Product product) {
        SaveProductResponse response = new SaveProductResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setMessage(SAVE_PROD_SUCC_RS);
        response.setProduct(product);
        return response;
    }

    @Override
    public SaveProductResponse buildSaveProductErrorResponse(String message) {
        SaveProductResponse response = new SaveProductResponse();
        response.setStatus(ResponseStatus.ERROR);
        response.setMessage(message);
        return response;
    }
}
