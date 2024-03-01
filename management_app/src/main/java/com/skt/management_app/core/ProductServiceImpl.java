package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.SaveProductResponse;
import com.skt.management_app.model.SelectProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private SelectProductService selectProductService;

    @Autowired
    private SaveProductService saveProductService;

    @Override
    public SelectProductResponse selectAll() {
        return selectProductService.selectAll();
    }

    @Override
    public SaveProductResponse save(Product product) {
        return saveProductService.save(product);
    }
}
