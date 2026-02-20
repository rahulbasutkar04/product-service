package com.project.store.domain.product.response;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {

    private boolean availability;

    private Category category;

    private LocalDateTime createdAt;

    private String createdBy;

    private Integer id;

    private LocalDateTime modifiedAt;

    private String modifiedBy;

    private String productName;

    private Integer productQuantity;

    public static ProductResponse buildProductResponseForAdmin(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productQuantity(product.getProductQuantity())
                .availability(product.isAvailability())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .createdBy(product.getCreatedBy())
                .modifiedBy(product.getModifiedBy())
                .build();
    }

}