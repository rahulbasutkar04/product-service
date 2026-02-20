package com.project.store.domain.product.request;

import com.project.store.domain.product.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request Pojo {@link com.project.store.domain.product.Product}
 */
@Data
public class ProductRequest {

    private boolean availability;

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Product name should not be empty")
    private String productName;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private Integer productQuantity;

}