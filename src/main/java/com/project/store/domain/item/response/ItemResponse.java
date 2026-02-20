package com.project.store.domain.item.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author rahul
 */
@Data
@Builder
public class ItemResponse {

    private Integer itemId;

    private Integer productId;

    private String productName;

    private Integer quantity;

}