package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.SaveProductResponse;
import com.skt.management_app.model.SelectProductResponse;

public interface ProductService {
    SelectProductResponse selectAll();

    SaveProductResponse save(Product product);
}
