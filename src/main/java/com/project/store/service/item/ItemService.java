package com.project.store.service.item;

import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;

import java.util.List;

/**
 * @author rahul
 *
 */
public interface ItemService {

    ItemResponse addItemService(ItemRequest request,String email);

    List<ItemResponse> getItemsByUserService(String email);

    List<ItemResponse> getItemsByProductService(Integer productId);


}
