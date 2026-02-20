package com.project.store.domain.product.response;


import com.project.store.domain.product.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseConsumer {


    private Category category;

    private String productName;

    private Integer productQuantity;

}
