package com.project.store.service.auth;

import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import com.project.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AppUserDetailsService}.
 */
@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .name("Test User")
                .password("encodedPassword")
                .contact("9876543210")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when user exists")
    void shouldBeAbleToReturnUserDetailsWhenUserExists() {
        // arrange
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        // act
        UserDetails details = appUserDetailsService.loadUserByUsername("user@test.com");

        // assert
        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo("user@test.com");
        assertThat(details.getPassword()).isEqualTo("encodedPassword");
        assertThat(details.getAuthorities()).hasSize(1);
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        verify(userRepository).findByEmail("user@test.com");
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException when user not found")
    void shouldBeAbleToThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // arrange
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> appUserDetailsService.loadUserByUsername("unknown@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Email not found for:");
        verify(userRepository).findByEmail("unknown@test.com");
    }

    @Test
    @DisplayName("loadUserByUsername should include ADMIN role when user is admin")
    void shouldBeAbleToIncludeAdminRoleWhenUserIsAdmin() {
        // arrange
        user.setRole(UserRole.ROLE_ADMIN);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        // act
        UserDetails details = appUserDetailsService.loadUserByUsername("admin@test.com");

        // assert
        assertThat(details.getAuthorities()).hasSize(1);
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}
