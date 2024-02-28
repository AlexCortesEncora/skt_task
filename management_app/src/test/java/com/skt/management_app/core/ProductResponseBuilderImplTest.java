package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.ResponseStatus;
import com.skt.management_app.model.SelectProductResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProductResponseBuilderImplTest {

    private ProductResponseBuilder responseBuilder;

    @Before
    public void setUp() {
        responseBuilder = new ProductResponseBuilderImpl();
    }

    @Test
    public void testBuildSelectProductsSuccessResponse() {
        List<Product> products = new ArrayList<>();
        SelectProductResponse response = responseBuilder.buildSelectProductsSuccessResponse(products);
        assertThat(response.getStatus(), is(ResponseStatus.SUCCESS));
        assertThat(response.getProducts(), is(products));
    }

    @Test
    public void testBuildSelectProductsErrorResponse() {
        String message = "This is a test";
        SelectProductResponse response = responseBuilder.buildSelectProductsErrorResponse(message);
        assertThat(response.getStatus(), is(ResponseStatus.ERROR));
        assertTrue(response.getProducts().isEmpty());
        assertThat(response.getMessage(), is(message));
    }
}