package com.project.store.service.product;

import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.response.ProductResponse;

public interface ProductService {

    ProductResponse createProductService(ProductRequest productRequest, String createdBy);

}
