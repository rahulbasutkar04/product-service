package com.project.store.repository;

import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for {@link UserRepository} using H2.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("should save and find user by email")
    void shouldBeAbleToSaveAndFindUserByEmail() {
        // arrange
        User user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .password("encoded")
                .contact("9876543210")
                .role(UserRole.ROLE_USER)
                .build();

        // act
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("should return empty when email not found")
    void shouldBeAbleToReturnEmptyWhenEmailNotFound() {
        // arrange
        // (no data)

        // act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should persist admin user")
    void shouldBeAbleToPersistAdminUser() {
        // arrange
        User admin = User.builder()
                .email("admin@example.com")
                .name("Admin")
                .password("encoded")
                .contact("9876543210")
                .role(UserRole.ROLE_ADMIN)
                .build();

        // act
        User saved = userRepository.save(admin);
        entityManager.flush();

        // assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("admin@example.com");
        assertThat(saved.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }
}
