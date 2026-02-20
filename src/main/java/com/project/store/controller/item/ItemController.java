package com.project.store.controller.item;

import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;
import com.project.store.service.item.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Item Controller
 */
@RestController
@RequestMapping("/api/v1/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * API to Add Item [USER]
     *
     * @param authentication {@link Authentication}
     * @param request {@link ItemRequest}
     * @return {@link ItemResponse}
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/secure")
    public ResponseEntity <ItemResponse> addItem(Authentication authentication,
                                                 @Valid @RequestBody ItemRequest request) {

        String email=authentication.getName();

        ItemResponse response = itemService.addItemService(request,email);

        return new ResponseEntity <>(response, HttpStatus.CREATED);
    }

    /**
     * API to Get All Added Items of Logged-in User [USER]
     *
     * @param authentication {@link Authentication}
     * @return {@link List<ItemResponse>}
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/secure")
    public ResponseEntity<List<ItemResponse>> getMyItems(Authentication authentication) {

        String email = authentication.getName();

        List<ItemResponse> response =
                itemService.getItemsByUserService(email);

        return ResponseEntity.ok(response);
    }


    /**
     * API to Get All Items by Product Id [ADMIN]
     *
     * @param productId {@link Integer}
     * @return {@link List<ItemResponse>}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/product/{productId}/secure")
    public ResponseEntity<List<ItemResponse>> getItemsByProduct(
            @PathVariable Integer productId) {

        List<ItemResponse> response =
                itemService.getItemsByProductService(productId);

        return ResponseEntity.ok(response);
    }


}
