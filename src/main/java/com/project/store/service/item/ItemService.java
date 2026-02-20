package com.project.store.service.item;

import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;

/**
 * @author rahul
 *
 */
public interface ItemService {

    ItemResponse addItemService(ItemRequest request);

}
