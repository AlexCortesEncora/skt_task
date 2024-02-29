package com.skt.management_app.core;

import com.skt.management_app.model.ResponseStatus;
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
}