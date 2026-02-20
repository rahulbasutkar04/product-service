package com.project.store.service.product;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.request.ProductUpdateRequest;
import com.project.store.domain.product.response.ProductResponse;
import com.project.store.domain.product.response.ProductResponseConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author rahul
 */
public interface ProductService {

    ProductResponse createProductService(ProductRequest productRequest, String createdBy);

    ProductResponse updateProductService(Integer productId, ProductUpdateRequest updateRequest, String updatedBy);

    ProductResponse getProductByIdService(Integer id);

    Page<ProductResponse> getAllProductsService(Category category, Pageable pageable);

    Page<ProductResponseConsumer> getAvailableProductsService(Category category, Pageable pageable);

}
