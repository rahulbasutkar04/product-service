package com.project.store.service.item.impl;

import com.project.store.domain.item.Item;
import com.project.store.domain.item.request.ItemRequest;
import com.project.store.domain.item.response.ItemResponse;
import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationException;
import com.project.store.repository.ItemRepository;
import com.project.store.repository.ProductRepository;
import com.project.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ItemImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ItemImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemImpl itemService;

    private User user;
    private Product product;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .name("Test User")
                .password("encoded")
                .contact("9876543210")
                .role(UserRole.ROLE_USER)
                .build();

        product = Product.builder()
                .id(1)
                .productName("Test Product")
                .productQuantity(10)
                .category(Category.ELECTRONICS)
                .availability(true)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setProductId(1);
        itemRequest.setQuantity(2);
    }

    @Nested
    @DisplayName("addItemService")
    class AddItemService {

        @Test
        @DisplayName("should add item successfully when valid request and user")
        void shouldBeAbleToAddItemWhenValidRequestAndUser() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> {
                Item i = inv.getArgument(0);
                i.setId(100);
                return i;
            });

            // act
            ItemResponse response = itemService.addItemService(itemRequest, "user@test.com");

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getItemId()).isEqualTo(100);
            assertThat(response.getProductId()).isEqualTo(1);
            assertThat(response.getProductName()).isEqualTo("Test Product");
            assertThat(response.getQuantity()).isEqualTo(2);
            verify(productRepository).save(product);
            verify(itemRepository).save(any(Item.class));
        }

        @Test
        @DisplayName("should throw when email is null")
        void shouldBeAbleToThrowWhenEmailIsNull() {
            // arrange
            // (no mocks needed)

            // act & assert
            assertThatThrownBy(() -> itemService.addItemService(itemRequest, null))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("email");
                    });
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldBeAbleToThrowWhenUserNotFound() {
            // arrange
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> itemService.addItemService(itemRequest, "unknown@test.com"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("User not found");
                    });
            verify(productRepository, never()).findById(any());
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when product not found")
        void shouldBeAbleToThrowWhenProductNotFound() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> itemService.addItemService(itemRequest, "user@test.com"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Product not found");
                    });
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when product is not available")
        void shouldBeAbleToThrowWhenProductIsNotAvailable() {
            // arrange
            product.setAvailability(false);
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // act & assert
            assertThatThrownBy(() -> itemService.addItemService(itemRequest, "user@test.com"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.INVALID_REQUEST);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("not available");
                    });
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when insufficient stock")
        void shouldBeAbleToThrowWhenInsufficientStock() {
            // arrange
            product.setProductQuantity(1);
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // act & assert
            assertThatThrownBy(() -> itemService.addItemService(itemRequest, "user@test.com"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.INVALID_REQUEST);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Insufficient stock");
                    });
            verify(itemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getItemsByUserService")
    class GetItemsByUserService {

        @Test
        @DisplayName("should return items for user")
        void shouldBeAbleToReturnItemsForUser() {
            // arrange
            Item item = Item.builder().id(1).product(product).user(user).quantity(2).build();
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(itemRepository.findByUser(user)).thenReturn(List.of(item));

            // act
            List<ItemResponse> result = itemService.getItemsByUserService("user@test.com");

            // assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getItemId()).isEqualTo(1);
            assertThat(result.get(0).getProductId()).isEqualTo(1);
            assertThat(result.get(0).getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return empty list when user has no items")
        void shouldBeAbleToReturnEmptyListWhenUserHasNoItems() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(itemRepository.findByUser(user)).thenReturn(List.of());

            // act
            List<ItemResponse> result = itemService.getItemsByUserService("user@test.com");

            // assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw when email is blank")
        void shouldBeAbleToThrowWhenEmailIsBlank() {
            // arrange
            // (no mocks needed)

            // act & assert
            assertThatThrownBy(() -> itemService.getItemsByUserService(""))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                    });
            assertThatThrownBy(() -> itemService.getItemsByUserService(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldBeAbleToThrowWhenUserNotFoundForGetItems() {
            // arrange
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> itemService.getItemsByUserService("unknown@test.com"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> assertThat(((ValidationException) ex).getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("getItemsByProductService")
    class GetItemsByProductService {

        @Test
        @DisplayName("should return items for product")
        void shouldBeAbleToReturnItemsForProduct() {
            // arrange
            Item item = Item.builder().id(1).product(product).user(user).quantity(2).build();
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(itemRepository.findByProduct(product)).thenReturn(List.of(item));

            // act
            List<ItemResponse> result = itemService.getItemsByProductService(1);

            // assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getItemId()).isEqualTo(1);
            assertThat(result.get(0).getProductId()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return empty list when product has no items")
        void shouldBeAbleToReturnEmptyListWhenProductHasNoItems() {
            // arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(itemRepository.findByProduct(product)).thenReturn(List.of());

            // act
            List<ItemResponse> result = itemService.getItemsByProductService(1);

            // assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw when productId is null")
        void shouldBeAbleToThrowWhenProductIdIsNull() {
            // arrange
            // (no mocks needed)

            // act & assert
            assertThatThrownBy(() -> itemService.getItemsByProductService(null))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("productId");
                    });
        }

        @Test
        @DisplayName("should throw when product not found")
        void shouldBeAbleToThrowWhenProductNotFoundForGetItems() {
            // arrange
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> itemService.getItemsByProductService(99))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.ENTITY_NOT_FOUND);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Product not found");
                    });
        }
    }
}
