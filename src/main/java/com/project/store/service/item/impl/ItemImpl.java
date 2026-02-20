package com.project.store.service.item.impl;

import com.project.store.domain.item.Item;
import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;
import com.project.store.domain.product.Product;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationError;
import com.project.store.exception.ValidationErrorType;
import com.project.store.exception.ValidationException;
import com.project.store.repository.ItemRepository;
import com.project.store.repository.ProductRepository;
import com.project.store.service.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rahul
 */
@Service
public class ItemImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Add Item [USER]
     *
     * @param request {@link ItemRequest}
     * @return {@link ItemResponse}
     */
    @Override
    @Transactional
    public ItemResponse addItemService(ItemRequest request) {

        Product product = productRepository.findById(
                        Long.valueOf(request.getProductId()))
                .orElseThrow(() -> new ValidationException(
                        new ValidationError(
                                "Product not found",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()),
                        ErrorResponseEnum.ENTITY_NOT_FOUND));

        //  Validate Availability
        validateProductAvailability(product);

        //  Validate Stock
        validateStock(product, request.getQuantity());

        //  Update Product Stock
        updateProductStock(product, request.getQuantity());

        Item item = Item.builder()
                .product(product)
                .quantity(request.getQuantity())
                .build();

        Item savedItem = itemRepository.save(item);

        return ItemResponse.builder()
                .itemId(savedItem.getId())
                .productId(product.getId())
                .productName(product.getProductName())
                .quantity(savedItem.getQuantity())
                .build();
    }

    /**
     * Validate Product availability
     *
     * @param product {@link Product}
     */
    private void validateProductAvailability(Product product) {

        if (! product.isAvailability()) {
            throw new ValidationException(
                    new ValidationError(
                            "Product is not available",
                            ValidationErrorType.INVALID_REQUEST.getErrorType()),
                    ErrorResponseEnum.INVALID_REQUEST);
        }
    }

    /**
     * Helper method to check validate stock
     *
     * @param product      {@link Product}
     * @param requestedQty {@link Integer}
     */
    private void validateStock(Product product, Integer requestedQty) {

        if (product.getProductQuantity() < requestedQty) {
            throw new ValidationException(
                    new ValidationError(
                            "Insufficient stock available",
                            ValidationErrorType.INVALID_REQUEST.getErrorType()),
                    ErrorResponseEnum.INVALID_REQUEST);
        }
    }

    /**
     * Helper method to update quantity
     *
     * @param product      {@link Product}
     * @param requestedQty {@link Integer}
     */
    private void updateProductStock(Product product, Integer requestedQty) {

        int updatedQty = product.getProductQuantity() - requestedQty;

        product.setProductQuantity(updatedQty);
        product.setAvailability(updatedQty > 0);

        productRepository.save(product);
    }

}
