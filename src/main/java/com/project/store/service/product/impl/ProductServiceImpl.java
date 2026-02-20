package com.project.store.service.product.impl;

import com.project.store.domain.product.Product;
import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.response.ProductResponse;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationError;
import com.project.store.exception.ValidationErrorType;
import com.project.store.exception.ValidationException;
import com.project.store.repository.ProductRepository;
import com.project.store.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductResponse createProductService(ProductRequest productRequest,
                                                String createdBy) {

        try {

            //  Duplicate Check
            boolean productExists =
                    productRepository.existsByProductNameAndCategory(
                            productRequest.getProductName(),
                            productRequest.getCategory());

            if (productExists) {

                throw new ValidationException(
                        new ValidationError(
                                "Product already exists in this category",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()
                        ),
                        ErrorResponseEnum.DUPLICATE_REQUEST
                );
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

            Product savedProduct = productRepository.save(product);

            return ProductResponse.buildProductResponseForAdmin(savedProduct);

        } catch (ValidationException validationException) {
            throw validationException;

        } catch (Exception exception) {

            throw new ValidationException(
                    new ValidationError(
                            "Failed to create product",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()
                    ),
                    ErrorResponseEnum.UNPROCESSABLE_ENTITY
            );
        }
    }

}
