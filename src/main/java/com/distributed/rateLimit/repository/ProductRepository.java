package com.distributed.rateLimit.repository;

import com.distributed.rateLimit.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { }