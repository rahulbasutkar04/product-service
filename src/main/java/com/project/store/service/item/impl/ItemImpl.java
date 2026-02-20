package com.project.store.service.item.impl;

import com.project.store.domain.item.Item;
import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;
import com.project.store.domain.product.Product;
import com.project.store.domain.user.User;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationError;
import com.project.store.exception.ValidationErrorType;
import com.project.store.exception.ValidationException;
import com.project.store.repository.ItemRepository;
import com.project.store.repository.ProductRepository;
import com.project.store.repository.UserRepository;
import com.project.store.service.item.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author rahul
 */
@Service
public class ItemImpl implements ItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemImpl.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Add Item [USER]
     *
     * @param request {@link ItemRequest}
     * @return {@link ItemResponse}
     */
    @Override
    @Transactional
    public ItemResponse addItemService(ItemRequest request, String email) {

        try {

            if (email == null) {
                throw new ValidationException(
                        new ValidationError(
                                "'email' can not be empty or null",
                                ValidationErrorType.REQUIRED_FIELD_MISSING.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
            }

            User user = validateUserExists(email);

            Product product = productRepository.findById(
                    Long.valueOf(request.getProductId())).orElseThrow(() -> new ValidationException(
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
                    .user(user)
                    .build();

            Item savedItem = itemRepository.save(item);

            return ItemResponse.builder()
                    .itemId(savedItem.getId())
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .quantity(savedItem.getQuantity())
                    .build();
        } catch (ValidationException validationException) {
            throw validationException;
        } catch (Exception exception) {
            LOG.error("Error occurred inside 'addItemService' : {}", exception.getMessage(), exception);
            throw new ValidationException(
                    new ValidationError(
                            "Failed to add item inside 'addItemService'",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
        }
    }


    private User validateUserExists(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException(
                        new ValidationError(
                                "User not found",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()),
                        ErrorResponseEnum.ENTITY_NOT_FOUND));
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


    /**
     * Get all item of user
     *
     * @param email {@link String}
     * @return {@link List<ItemResponse>}
     */
    @Override
    public List <ItemResponse> getItemsByUserService(String email) {

        try {

            if (! StringUtils.hasLength(email)) {
                throw new ValidationException(
                        new ValidationError(
                                "'email' can not be empty or null",
                                ValidationErrorType.REQUIRED_FIELD_MISSING.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
            }

            User user = validateUserExists(email);

            List <Item> items = itemRepository.findByUser(user);

            return items.stream()
                    .map(ItemResponse::buildItemResponse)
                    .toList();
        } catch (ValidationException validationException) {
            throw validationException;
        } catch (Exception exception) {
            LOG.error("Error occurred inside 'getItemsByUserService' for email {} : {}", email, exception.getMessage(), exception);
            throw new ValidationException(
                    new ValidationError(
                            "Failed to fetch items inside 'getItemsByUserService'",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);

        }
    }

    @Override
    public List <ItemResponse> getItemsByProductService(Integer productId) {

        try {

            if (productId == null) {
                throw new ValidationException(
                        new ValidationError(
                                "'productId' can not be empty or null",
                                ValidationErrorType.REQUIRED_FIELD_MISSING.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
            }
            Product product = productRepository.findById(
                    Long.valueOf(productId)).orElseThrow(() -> new ValidationException(
                    new ValidationError(
                            "Product not found",
                            ValidationErrorType.INVALID_REQUEST.getErrorType()),
                    ErrorResponseEnum.ENTITY_NOT_FOUND));

            List <Item> items = itemRepository.findByProduct(product);

            return items.stream()
                    .map(ItemResponse::buildItemResponse)
                    .toList();
        } catch (ValidationException validationException) {
            throw validationException;
        } catch (Exception exception) {
            LOG.error("Error occurred inside 'getItemsByProductService' for productId {} : {}", productId, exception.getMessage(), exception);
            throw new ValidationException(
                    new ValidationError(
                            "Failed to fetch items inside 'getItemsByProductService'",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()), ErrorResponseEnum.UNPROCESSABLE_ENTITY);
        }
    }


}
