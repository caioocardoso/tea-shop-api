package com.caiooccardoso.tea_shop_api.repositories;

import com.caiooccardoso.tea_shop_api.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);
    Product getProductById(Long id);
}
