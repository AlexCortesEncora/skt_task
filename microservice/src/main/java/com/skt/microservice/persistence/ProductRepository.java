package com.skt.microservice.persistence;

import com.skt.microservice.persistence.entity.ProductEntity;

import java.util.List;

public interface ProductRepository {
    public List<ProductEntity> getAll();
}
