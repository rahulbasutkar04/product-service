package com.project.store.repository;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author rahul
 * Repository for {@link Product}
 */
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    boolean existsByProductNameAndCategory(String productName, Category category);

    Page<Product> findByCategory(Category category, Pageable pageable);


    Page<Product> findByCategoryAndAvailabilityTrue(Category category, Pageable pageable);


    Page<Product> findByAvailabilityTrue(Pageable pageable);


}
