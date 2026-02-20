package com.project.store.service.auth;

import com.project.store.domain.user.auth.request.AuthRequest;
import com.project.store.domain.user.auth.response.AuthResponse;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationException;
import com.project.store.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthService}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setEmail("user@test.com");
        authRequest.setPassword("password123");

        userDetails = new User("user@test.com", "encoded", List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));
    }

    @Nested
    @DisplayName("loginService")
    class LoginService {

        @Test
        @DisplayName("should return tokens when authentication succeeds")
        void shouldBeAbleToReturnTokensWhenAuthenticationSucceeds() {
            // arrange
            when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
            when(jwtUtil.generateToken(userDetails)).thenReturn("accessToken");
            when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("refreshToken");

            // act
            AuthResponse response = authService.loginService(authRequest);

            // assert
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("user@test.com");
            assertThat(response.getToken()).isEqualTo("accessToken");
            assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userDetailsService).loadUserByUsername("user@test.com");
            verify(jwtUtil).generateToken(userDetails);
            verify(jwtUtil).generateRefreshToken(userDetails);
        }

        @Test
        @DisplayName("should throw ValidationException on BadCredentialsException")
        void shouldBeAbleToThrowValidationExceptionOnBadCredentials() {
            // arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // act & assert
            assertThatThrownBy(() -> authService.loginService(authRequest))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.INVALID_REQUEST);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Email or password is incorrect");
                    });
            verify(userDetailsService, never()).loadUserByUsername(any());
        }

        @Test
        @DisplayName("should throw ValidationException on DisabledException")
        void shouldBeAbleToThrowValidationExceptionOnDisabledAccount() {
            // arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new DisabledException("Account disabled"));

            // act & assert
            assertThatThrownBy(() -> authService.loginService(authRequest))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNAUTHORIZED);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Account is disabled");
                    });
        }

        @Test
        @DisplayName("should throw ValidationException on generic Exception")
        void shouldBeAbleToThrowValidationExceptionOnGenericException() {
            // arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Unexpected"));

            // act & assert
            assertThatThrownBy(() -> authService.loginService(authRequest))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(ex -> {
                        ValidationException ve = (ValidationException) ex;
                        assertThat(ve.getErrorResponse()).isEqualTo(ErrorResponseEnum.UNAUTHORIZED);
                        assertThat(ve.getValidationError().getErrorMessage()).contains("Authentication Failed");
                    });
        }
    }
}
