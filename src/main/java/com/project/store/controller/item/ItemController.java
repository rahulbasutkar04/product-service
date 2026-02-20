package com.project.store.controller.item;

import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;
import com.project.store.service.item.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Item Controller
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * API to Add Item [USER]
     *
     * @param request {@link ItemRequest}
     * @return {@link ItemResponse}
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add/secure")
    public ResponseEntity <ItemResponse> addItem(@Valid @RequestBody ItemRequest request) {

        ItemResponse response = itemService.addItemService(request);

        return new ResponseEntity <>(response, HttpStatus.CREATED);
    }

}
