package com.project.store.repository;

import com.project.store.domain.item.Item;
import com.project.store.domain.product.Category;
import com.project.store.domain.product.Product;
import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for {@link ItemRepository} using H2.
 */
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("user@test.com")
                .name("Test User")
                .password("encoded")
                .contact("9876543210")
                .role(UserRole.ROLE_USER)
                .build();
        user = userRepository.save(user);

        product = Product.builder()
                .productName("Laptop")
                .productQuantity(10)
                .category(Category.ELECTRONICS)
                .availability(true)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();
        product = productRepository.save(product);
        entityManager.flush();
    }

    @Test
    @DisplayName("should save and find item by id")
    void shouldBeAbleToSaveAndFindItemById() {
        // arrange
        Item item = Item.builder()
                .product(product)
                .user(user)
                .quantity(2)
                .build();

        // act
        Item saved = itemRepository.save(item);
        entityManager.flush();
        entityManager.clear();
        Item found = itemRepository.findById(saved.getId()).orElseThrow();

        // assert
        assertThat(saved.getId()).isNotNull();
        assertThat(found.getQuantity()).isEqualTo(2);
        assertThat(found.getProduct().getProductName()).isEqualTo("Laptop");
        assertThat(found.getUser().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("findByUser should return items for user")
    void shouldBeAbleToReturnItemsForUser() {
        // arrange
        Item item1 = Item.builder().product(product).user(user).quantity(1).build();
        itemRepository.save(item1);
        entityManager.flush();
        entityManager.clear();
        User loadedUser = userRepository.findByEmail("user@test.com").orElseThrow();

        // act
        List<Item> items = itemRepository.findByUser(loadedUser);

        // assert
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByUser should return empty list when user has no items")
    void shouldBeAbleToReturnEmptyListWhenUserHasNoItems() {
        // arrange
        User newUser = User.builder()
                .email("newuser@test.com")
                .name("New User")
                .password("encoded")
                .contact("9876543210")
                .role(UserRole.ROLE_USER)
                .build();
        newUser = userRepository.save(newUser);
        entityManager.flush();

        // act
        List<Item> items = itemRepository.findByUser(newUser);

        // assert
        assertThat(items).isEmpty();
    }

    @Test
    @DisplayName("findByProduct should return items for product")
    void shouldBeAbleToReturnItemsForProduct() {
        // arrange
        Item item = Item.builder().product(product).user(user).quantity(3).build();
        itemRepository.save(item);
        entityManager.flush();
        entityManager.clear();
        Product loadedProduct = productRepository.findById(product.getId().longValue()).orElseThrow();

        // act
        List<Item> items = itemRepository.findByProduct(loadedProduct);

        // assert
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(3);
    }
}
