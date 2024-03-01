package com.skt.management_app.core;

import com.skt.management_app.model.Product;
import com.skt.management_app.model.SaveProductResponse;

public interface SaveProductService {
    SaveProductResponse save(Product product);
}
