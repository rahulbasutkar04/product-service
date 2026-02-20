package com.project.store.service.user.impl;

import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import com.project.store.domain.user.request.UserRequest;
import com.project.store.domain.user.response.UserResponse;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationException;
import com.project.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setEmail("user@test.com");
        userRequest.setName("Test User");
        userRequest.setPassword("password123");
        userRequest.setContact("9876543210");

        existingUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .name("Test User")
                .password("encoded")
                .contact("9876543210")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Nested
    @DisplayName("createAdminUserService")
    class CreateAdminUserService {

        @Test
        @DisplayName("should create admin user successfully when email not exists")
        void shouldBeAbleToCreateAdminUserWhenEmailNotExists() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });

            // act
            UserResponse response = userService.createAdminUserService(userRequest);

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("user@test.com");
            assertThat(response.getName()).isEqualTo("Test User");
            assertThat(response.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
            assertThat(response.getContact()).isEqualTo("9876543210");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw when user already exists")
        void shouldBeAbleToThrowWhenUserAlreadyExistsOnCreateAdmin() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));

            // act & assert
            assertThatThrownBy(() -> userService.createAdminUserService(userRequest))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.USER_CONFLICT);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("already exists");
                    });
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should encode password before saving")
        void shouldBeAbleToEncodePasswordBeforeSavingAdmin() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            // act
            userService.createAdminUserService(userRequest);

            // assert
            verify(passwordEncoder).encode("password123");
        }
    }

    @Nested
    @DisplayName("createUserService")
    class CreateUserService {

        @Test
        @DisplayName("should create consumer user successfully when email not exists")
        void shouldBeAbleToCreateUserWhenEmailNotExists() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });

            // act
            UserResponse response = userService.createUserService(userRequest);

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("user@test.com");
            assertThat(response.getName()).isEqualTo("Test User");
            assertThat(response.getRole()).isEqualTo(UserRole.ROLE_USER);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw when user already exists")
        void shouldBeAbleToThrowWhenUserAlreadyExistsOnCreateUser() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));

            // act & assert
            assertThatThrownBy(() -> userService.createUserService(userRequest))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.USER_CONFLICT);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("already exists");
                    });
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ValidationException on DataAccessException")
        void shouldBeAbleToThrowValidationExceptionOnDataAccessException() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenThrow(new org.springframework.dao.DataIntegrityViolationException("DB error"));

            // act & assert
            assertThatThrownBy(() -> userService.createUserService(userRequest))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Failed to register User");
                    });
        }
    }

    @Nested
    @DisplayName("getUserByEmailIdService")
    class GetUserByEmailIdService {

        @Test
        @DisplayName("should return user when found")
        void shouldBeAbleToReturnUserWhenFound() {
            // arrange
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));

            // act
            UserResponse response = userService.getUserByEmailIdService("user@test.com");

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("user@test.com");
            assertThat(response.getName()).isEqualTo("Test User");
            assertThat(response.getRole()).isEqualTo(UserRole.ROLE_USER);
        }

        @Test
        @DisplayName("should throw when email is blank")
        void shouldBeAbleToThrowWhenEmailIsBlank() {
            // arrange
            // (no mocks needed)

            // act & assert
            assertThatThrownBy(() -> userService.getUserByEmailIdService(""))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.VALIDATION_ERROR);
                    });
            assertThatThrownBy(() -> userService.getUserByEmailIdService(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldBeAbleToThrowWhenUserNotFound() {
            // arrange
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> userService.getUserByEmailIdService("unknown@test.com"))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNPROCESSABLE_ENTITY);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("User Not found");
                    });
        }
    }
}
