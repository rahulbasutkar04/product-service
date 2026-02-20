package com.project.store.service.product.impl;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.request.ProductUpdateRequest;
import com.project.store.domain.product.response.ProductResponse;
import com.project.store.domain.product.response.ProductResponseConsumer;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationException;
import com.project.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProductServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1)
                .productName("Laptop")
                .productQuantity(5)
                .category(Category.ELECTRONICS)
                .availability(true)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();

        productRequest = new ProductRequest();
        productRequest.setProductName("Laptop");
        productRequest.setProductQuantity(10);
        productRequest.setCategory(Category.ELECTRONICS);

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("createProductService")
    class CreateProductService {

        @Test
        @DisplayName("should create product successfully when not duplicate")
        void shouldBeAbleToCreateProductWhenNotDuplicate() {
            // arrange
            when(productRepository.existsByProductNameAndCategory("Laptop", Category.ELECTRONICS)).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
                Product p = inv.getArgument(0);
                p.setId(1);
                return p;
            });

            // act
            ProductResponse response = productService.createProductService(productRequest, "admin@test.com");

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getProductName()).isEqualTo("Laptop");
            assertThat(response.getProductQuantity()).isEqualTo(10);
            assertThat(response.getCategory()).isEqualTo(Category.ELECTRONICS);
            assertThat(response.isAvailability()).isTrue();
            assertThat(response.getCreatedBy()).isEqualTo("admin@test.com");
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("should set availability false when quantity is zero")
        void shouldBeAbleToSetAvailabilityFalseWhenQuantityIsZero() {
            // arrange
            productRequest.setProductQuantity(0);
            when(productRepository.existsByProductNameAndCategory("Laptop", Category.ELECTRONICS)).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // act
            ProductResponse response = productService.createProductService(productRequest, "admin");

            // assert
            assertThat(response.isAvailability()).isFalse();
            assertThat(response.getProductQuantity()).isZero();
        }

        @Test
        @DisplayName("should throw when product already exists in category")
        void shouldBeAbleToThrowWhenProductAlreadyExistsInCategory() {
            // arrange
            when(productRepository.existsByProductNameAndCategory("Laptop", Category.ELECTRONICS)).thenReturn(true);

            // act & assert
            assertThatThrownBy(() -> productService.createProductService(productRequest, "admin"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.DUPLICATE_REQUEST);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("already exists");
                    });
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateProductService")
    class UpdateProductService {

        @Test
        @DisplayName("should update product successfully")
        void shouldBeAbleToUpdateProductSuccessfully() {
            // arrange
            ProductUpdateRequest updateRequest = new ProductUpdateRequest();
            updateRequest.setProductName("Updated Laptop");
            updateRequest.setProductQuantity(20);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // act
            ProductResponse response = productService.updateProductService(1, updateRequest, "admin@test.com");

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getProductName()).isEqualTo("Updated Laptop");
            assertThat(response.getProductQuantity()).isEqualTo(20);
            assertThat(response.isAvailability()).isTrue();
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("should throw when id is null")
        void shouldBeAbleToThrowWhenIdIsNull() {
            // arrange
            ProductUpdateRequest updateRequest = new ProductUpdateRequest();

            // act & assert
            assertThatThrownBy(() -> productService.updateProductService(null, updateRequest, "admin"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("id");
                    });
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when product not found")
        void shouldBeAbleToThrowWhenProductNotFoundOnUpdate() {
            // arrange
            when(productRepository.findById(999L)).thenReturn(Optional.empty());
            ProductUpdateRequest updateRequest = new ProductUpdateRequest();

            // act & assert
            assertThatThrownBy(() -> productService.updateProductService(999, updateRequest, "admin"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Product not found");
                    });
        }

        @Test
        @DisplayName("should set availability false when quantity updated to zero")
        void shouldBeAbleToSetAvailabilityFalseWhenQuantityUpdatedToZero() {
            // arrange
            ProductUpdateRequest updateRequest = new ProductUpdateRequest();
            updateRequest.setProductQuantity(0);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // act
            ProductResponse response = productService.updateProductService(1, updateRequest, "admin");

            // assert
            assertThat(response.getProductQuantity()).isZero();
            assertThat(response.isAvailability()).isFalse();
        }

        @Test
        @DisplayName("should update only provided fields")
        void shouldBeAbleToUpdateOnlyProvidedFields() {
            // arrange
            ProductUpdateRequest updateRequest = new ProductUpdateRequest();
            updateRequest.setProductName("Only Name Updated");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // act
            ProductResponse response = productService.updateProductService(1, updateRequest, "admin");

            // assert
            assertThat(response.getProductName()).isEqualTo("Only Name Updated");
            assertThat(response.getProductQuantity()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("getProductByIdService")
    class GetProductByIdService {

        @Test
        @DisplayName("should return product when found")
        void shouldBeAbleToReturnProductWhenFound() {
            // arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // act
            ProductResponse response = productService.getProductByIdService(1);

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1);
            assertThat(response.getProductName()).isEqualTo("Laptop");
        }

        @Test
        @DisplayName("should throw when productId is null")
        void shouldBeAbleToThrowWhenProductIdIsNull() {
            // arrange
            // (no mocks needed)

            // act & assert
            assertThatThrownBy(() -> productService.getProductByIdService(null))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("productId");
                    });
        }

        @Test
        @DisplayName("should throw when product not found")
        void shouldBeAbleToThrowWhenProductNotFoundOnGet() {
            // arrange
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> productService.getProductByIdService(99))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("getAllProductsService")
    class GetAllProductsService {

        @Test
        @DisplayName("should return paged products when category is null")
        void shouldBeAbleToReturnPagedProductsWhenCategoryIsNull() {
            // arrange
            Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
            when(productRepository.findAll(pageable)).thenReturn(page);

            // act
            Page<ProductResponse> result = productService.getAllProductsService(null, pageable);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getProductName()).isEqualTo("Laptop");
        }

        @Test
        @DisplayName("should return paged products filtered by category")
        void shouldBeAbleToReturnPagedProductsFilteredByCategory() {
            // arrange
            Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
            when(productRepository.findByCategory(Category.ELECTRONICS, pageable)).thenReturn(page);

            // act
            Page<ProductResponse> result = productService.getAllProductsService(Category.ELECTRONICS, pageable);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCategory()).isEqualTo(Category.ELECTRONICS);
        }
    }

    @Nested
    @DisplayName("getAvailableProductsService")
    class GetAvailableProductsService {

        @Test
        @DisplayName("should return only available products when category is null")
        void shouldBeAbleToReturnAvailableProductsWhenCategoryIsNull() {
            // arrange
            Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
            when(productRepository.findByAvailabilityTrue(pageable)).thenReturn(page);

            // act
            Page<ProductResponseConsumer> result = productService.getAvailableProductsService(null, pageable);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getProductName()).isEqualTo("Laptop");
            assertThat(result.getContent().get(0).getProductQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return available products filtered by category")
        void shouldBeAbleToReturnAvailableProductsFilteredByCategory() {
            // arrange
            Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
            when(productRepository.findByCategoryAndAvailabilityTrue(Category.ELECTRONICS, pageable)).thenReturn(page);

            // act
            Page<ProductResponseConsumer> result = productService.getAvailableProductsService(Category.ELECTRONICS, pageable);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCategory()).isEqualTo(Category.ELECTRONICS);
        }
    }

    @Nested
    @DisplayName("deleteProductService")
    class DeleteProductService {

        @Test
        @DisplayName("should return success message when product is available")
        void shouldBeAbleToReturnSuccessMessageWhenProductIsAvailable() {
            // arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // act
            String result = productService.deleteProductService(1, "admin");

            // assert
            assertThat(result).isEqualTo("Product Deleted Succesfully");
            assertThat(product.isAvailability()).isFalse();
        }

        @Test
        @DisplayName("should throw when product not found")
        void shouldBeAbleToThrowWhenProductNotFoundOnDelete() {
            // arrange
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> productService.deleteProductService(999, "admin"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Product not found");
                    });
        }

        @Test
        @DisplayName("should throw when product already deleted (not available)")
        void shouldBeAbleToThrowWhenProductAlreadyDeleted() {
            // arrange
            product.setAvailability(false);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // act & assert
            assertThatThrownBy(() -> productService.deleteProductService(1, "admin"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.INVALID_REQUEST);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("already deleted");
                    });
            verify(productRepository, never()).save(any());
        }
    }
}
