package com.skt.microservice.persistence;

import com.skt.microservice.persistence.entity.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public List<ProductEntity> getAll() {
        List<ProductEntity> products = new ArrayList<>();
        return products;

    }
}
