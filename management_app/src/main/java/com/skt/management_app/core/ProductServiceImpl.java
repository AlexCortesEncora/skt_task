package com.skt.management_app.core;

import com.skt.management_app.model.SelectProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private SelectProductService selectProductService;

    @Override
    public SelectProductResponse selectAll() {
        return selectProductService.selectAll();
    }
}
