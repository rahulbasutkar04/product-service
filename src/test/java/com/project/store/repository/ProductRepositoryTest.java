package com.project.store.repository;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for {@link ProductRepository} using H2.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productName("Laptop")
                .productQuantity(10)
                .category(Category.ELECTRONICS)
                .availability(true)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();
    }

    @Test
    @DisplayName("should save and find product by id")
    void shouldBeAbleToSaveAndFindProductById() {
        // arrange
        // (product from @BeforeEach)

        // act
        Product saved = productRepository.save(product);
        entityManager.flush();
        entityManager.clear();
        Product found = productRepository.findById(saved.getId().longValue()).orElseThrow();

        // assert
        assertThat(saved.getId()).isNotNull();
        assertThat(found.getProductName()).isEqualTo("Laptop");
        assertThat(found.getCategory()).isEqualTo(Category.ELECTRONICS);
    }

    @Test
    @DisplayName("existsByProductNameAndCategory should return true when exists")
    void shouldBeAbleToReturnTrueFromExistsByProductNameAndCategoryWhenExists() {
        // arrange
        productRepository.save(product);
        entityManager.flush();

        // act
        boolean exists = productRepository.existsByProductNameAndCategory("Laptop", Category.ELECTRONICS);

        // assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByProductNameAndCategory should return false when not exists")
    void shouldBeAbleToReturnFalseFromExistsByProductNameAndCategoryWhenNotExists() {
        // arrange
        // (no product saved)

        // act
        boolean exists = productRepository.existsByProductNameAndCategory("Laptop", Category.ELECTRONICS);

        // assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByCategory should return paged products")
    void shouldBeAbleToReturnPagedProductsByCategory() {
        // arrange
        productRepository.save(product);
        Product toy = Product.builder()
                .productName("Teddy")
                .productQuantity(5)
                .category(Category.TOY)
                .availability(true)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();
        productRepository.save(toy);
        entityManager.flush();
        Pageable pageable = PageRequest.of(0, 10);

        // act
        Page<Product> page = productRepository.findByCategory(Category.ELECTRONICS, pageable);

        // assert
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getProductName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findByAvailabilityTrue should return only available products")
    void shouldBeAbleToReturnOnlyAvailableProducts() {
        // arrange
        productRepository.save(product);
        Product unavailable = Product.builder()
                .productName("Unavailable")
                .productQuantity(0)
                .category(Category.CLOTH)
                .availability(false)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();
        productRepository.save(unavailable);
        entityManager.flush();
        Pageable pageable = PageRequest.of(0, 10);

        // act
        Page<Product> page = productRepository.findByAvailabilityTrue(pageable);

        // assert
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getProductName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findByCategoryAndAvailabilityTrue should filter by category and availability")
    void shouldBeAbleToFilterByCategoryAndAvailabilityTrue() {
        // arrange
        productRepository.save(product);
        entityManager.flush();
        Pageable pageable = PageRequest.of(0, 10);

        // act
        Page<Product> page = productRepository.findByCategoryAndAvailabilityTrue(Category.ELECTRONICS, pageable);

        // assert
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getCategory()).isEqualTo(Category.ELECTRONICS);
        assertThat(page.getContent().get(0).isAvailability()).isTrue();
    }
}
