package com.project.store.repository;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    boolean existsByProductNameAndCategory(String productName, Category category);

}
