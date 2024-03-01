package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.ResponseStatus;
import com.skt.management_app.model.SaveProductResponse;
import com.skt.management_app.model.SelectProductResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private SelectProductService selectProductService;
    @Mock
    private SaveProductService saveProductService;

    @Test
    public void Given_SelectAll_When_SelectProductServiceReturnResponse_Then_ProductServiceShouldReturnEqualResponse() {
        SelectProductResponse responseExpected = new SelectProductResponse();
        responseExpected.setStatus(ResponseStatus.SUCCESS);
        responseExpected.setMessage("This is a test");
        responseExpected.setProducts(new ArrayList<>());
        when(selectProductService.selectAll()).thenReturn(responseExpected);

        SelectProductResponse response = productService.selectAll();
        assertThat(response, is(responseExpected));
    }

    @Test
    public void Given_Save_When_SaveProductServiceReturnResponse_Then_ProductServiceShouldReturnEqualResponse() {
        Product product = new Product();
        SaveProductResponse responseExpected = new SaveProductResponse();
        responseExpected.setStatus(ResponseStatus.SUCCESS);
        responseExpected.setMessage("This is a test");
        responseExpected.setProduct(product);

        when(saveProductService.save(product)).thenReturn(responseExpected);

        SaveProductResponse response = productService.save(product);
        assertThat(response, is(responseExpected));
    }
}