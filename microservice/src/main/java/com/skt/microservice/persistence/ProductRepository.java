package com.skt.microservice.persistence;

import com.skt.microservice.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query(value = "select * from find_all_products();", nativeQuery = true)
    List<ProductEntity> findAllProducts();

    @Query(value = "BEGIN; CALL public.select_products('result'); fetch all in \"result\";", nativeQuery = true)
    List<ProductEntity> selectAllProducts();
}
