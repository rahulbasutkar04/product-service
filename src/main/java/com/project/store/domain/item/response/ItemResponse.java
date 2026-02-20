package com.project.store.domain.item.response;

import com.project.store.domain.item.Item;
import lombok.Builder;
import lombok.Data;

/**
 * @author rahul
 */
@Data
@Builder(toBuilder = true)
public class ItemResponse {

    private Integer itemId;

    private Integer productId;

    private String productName;

    private Integer quantity;


    public static ItemResponse buildItemResponse(Item item)
    {
       return ItemResponse.builder()
               .itemId(item.getId())
               .productId(item.getProduct().getId())
               .productName(item.getProduct().getProductName())
               .quantity(item.getQuantity())
               .build();
    }
}