package com.project.store.domain.product.request;

import com.project.store.domain.product.Category;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Request pojo to update Product {@link com.project.store.domain.product.Product}
 */
@Data
public class ProductUpdateRequest {

    private String productName;

    @Min(value = 0, message = "Product quantity cannot be negative")
    private Integer productQuantity;

    private Category category;

    private Boolean availability;
}