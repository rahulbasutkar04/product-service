package com.project.store.service.product.impl;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.request.ProductUpdateRequest;
import com.project.store.domain.product.response.ProductResponse;
import com.project.store.domain.product.response.ProductResponseConsumer;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationError;
import com.project.store.exception.ValidationErrorType;
import com.project.store.exception.ValidationException;
import com.project.store.repository.ProductRepository;
import com.project.store.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author rahul
 * Service Class for {@link Product}
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create new Product [ADMIN]
     *
     * @param productRequest {@link ProductRequest}
     * @param createdBy      {@link String}
     * @return {@link ProductResponse}
     */
    @Override
    public ProductResponse createProductService(ProductRequest productRequest,
                                                String createdBy) {

        try {

            // Duplicate check
            boolean productExists = productRepository.existsByProductNameAndCategory(
                    productRequest.getProductName(),
                    productRequest.getCategory());

            if (productExists) {
                throw new ValidationException(
                        new ValidationError(
                                "Product already exists in this category",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()), ErrorResponseEnum.DUPLICATE_REQUEST);
            }

            //  Business Logic: Availability depends on quantity
            boolean availability =
                    productRequest.getProductQuantity() != null &&
                            productRequest.getProductQuantity() > 0;

            //  Create Product Entity
            Product product = Product.builder()
                    .productName(productRequest.getProductName())
                    .productQuantity(productRequest.getProductQuantity())
                    .category(productRequest.getCategory())
                    .availability(availability)
                    .createdBy(createdBy)
                    .modifiedBy(createdBy)
                    .build();

            // Save
            Product savedProduct = productRepository.save(product);

            return ProductResponse.buildProductResponseForAdmin(savedProduct);

        } catch (ValidationException validationException) {
            throw validationException;

        } catch (Exception exception) {
            throw new ValidationException(
                    new ValidationError(
                            "Failed to create product",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Update Product [ADMIN]
     *
     * @param id         {@link Product}_id
     * @param request    {@link ProductRequest}
     * @param modifiedBy {@link String}
     * @return {@link ProductResponse}
     */
    @Override
    public ProductResponse updateProductService(Integer id,
                                                ProductUpdateRequest request,
                                                String modifiedBy) {

        try {

            if (id == null) {
                throw new ValidationException(
                        new ValidationError(
                                "'id' can not be empty or null",
                                ValidationErrorType.REQUIRED_FIELD_MISSING.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
            }

            Product product = productRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new ValidationException(
                            new ValidationError(
                                    "Product not found",
                                    ValidationErrorType.INVALID_REQUEST.getErrorType()), ErrorResponseEnum.ENTITY_NOT_FOUND));

            //  Update only if field is present
            if (request.getProductName() != null) {
                product.setProductName(request.getProductName());
            }

            if (request.getProductQuantity() != null) {
                product.setProductQuantity(request.getProductQuantity());

                // auto availability logic
                product.setAvailability(request.getProductQuantity() > 0);
            }

            if (request.getCategory() != null) {
                product.setCategory(request.getCategory());
            }

            if (request.getAvailability() != null) {
                product.setAvailability(request.getAvailability());
            }

            product.setModifiedBy(modifiedBy);

            Product updatedProduct = productRepository.save(product);

            return ProductResponse.buildProductResponseForAdmin(updatedProduct);

        } catch (ValidationException validationException) {
            throw validationException;

        } catch (Exception exception) {

            throw new ValidationException(
                    new ValidationError(
                            "Failed to update product",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Get Product By Id  [ADMIN]
     *
     * @param productId {@link Product}_id
     * @return {@link ProductResponse}
     */
    @Override
    public ProductResponse getProductByIdService(Integer productId) {

        try {

            if (productId == null) {
                throw new ValidationException(
                        new ValidationError(
                                "'productId' can not be empty or null",
                                ValidationErrorType.REQUIRED_FIELD_MISSING.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
            }

            Product product = productRepository.findById(Long.valueOf(productId))
                    .orElseThrow(() -> new ValidationException(
                            new ValidationError(
                                    "Product not found",
                                    ValidationErrorType.INVALID_REQUEST.getErrorType()), ErrorResponseEnum.ENTITY_NOT_FOUND));

            return ProductResponse.buildProductResponseForAdmin(product);

        } catch (ValidationException validationException) {
            throw validationException;
        } catch (Exception exception) {
            LOG.error("Something went wrong inside 'getProductByIdService' {}", exception.getMessage());
            throw exception;
        }
    }

    /**
     * Get all by product in page with optional filter  [ADMIN]
     *
     * @param category {@link Category}
     * @param pageable {@link Pageable}
     * @return {@link Page<ProductResponse>}
     */
    @Override
    public Page <ProductResponse> getAllProductsService(Category category, Pageable pageable) {

        try {

            Page <Product> productPage;

            if (category != null) {
                productPage = productRepository.findByCategory(category, pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }

            return productPage.map(ProductResponse::buildProductResponseForAdmin);
        } catch (Exception exception) {
            throw exception;
        }
    }


    /**
     * Get all by product in page with optional filter [USER]
     *
     * @param category {@link Category}
     * @param pageable {@link Pageable}
     * @return {@link Page<ProductResponseConsumer>}
     */
    @Override
    public Page <ProductResponseConsumer> getAvailableProductsService(
            Category category,
            Pageable pageable) {

        try {

            Page <Product> productPage;

            if (category != null) {
                productPage =
                        productRepository.findByCategoryAndAvailabilityTrue(
                                category, pageable);
            } else {
                productPage =
                        productRepository.findByAvailabilityTrue(pageable);
            }

            return productPage.map(product ->
                    ProductResponseConsumer.builder()
                            .productName(product.getProductName())
                            .productQuantity(product.getProductQuantity())
                            .category(product.getCategory())
                            .build());

        } catch (Exception exception) {
            throw exception;
        }
    }

    @Override
    public String deleteProductService(Integer productId, String deletedBy) {
        try{

            Product product = productRepository.findById(Long.valueOf(productId))
                    .orElseThrow(() -> new ValidationException(
                            new ValidationError(
                                    "Product not found",
                                    ValidationErrorType.INVALID_REQUEST.getErrorType()), ErrorResponseEnum.ENTITY_NOT_FOUND));

            if (!product.isAvailability()) {
                throw new ValidationException(
                        new ValidationError(
                                "Product already deleted",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()),ErrorResponseEnum.INVALID_REQUEST);
            }

            product.setAvailability(false);


            return "Product Deleted Succesfully";


        }catch (ValidationException validationException)
        {
            throw validationException;
        }catch (Exception exception)
        {
            throw exception;
        }
    }

}
