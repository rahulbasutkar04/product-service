package com.project.store.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link JwtUtil}.
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
            "test-secret-32-bytes-long!!!!!!!!!".getBytes(StandardCharsets.UTF_8));

    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET_KEY);
        userDetails = new User("user@test.com", "password", List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("should generate non-null access token")
        void shouldBeAbleToGenerateNonNullAccessToken() {
            // arrange
            // (setup in @BeforeEach)

            // act
            String token = jwtUtil.generateToken(userDetails);

            // assert
            assertThat(token).isNotBlank();
        }

        @Test
        @DisplayName("should generate valid tokens on multiple calls")
        void shouldBeAbleToGenerateValidTokensOnMultipleCalls() {
            // arrange
            // (setup in @BeforeEach)

            // act
            String token1 = jwtUtil.generateToken(userDetails);
            String token2 = jwtUtil.generateToken(userDetails);

            // assert (both valid; may be equal if issued in same second)
            assertThat(token1).isNotBlank();
            assertThat(token2).isNotBlank();
            assertThat(jwtUtil.extractEmail(token1)).isEqualTo("user@test.com");
            assertThat(jwtUtil.extractEmail(token2)).isEqualTo("user@test.com");
        }
    }

    @Nested
    @DisplayName("generateRefreshToken")
    class GenerateRefreshToken {

        @Test
        @DisplayName("should generate non-null refresh token")
        void shouldBeAbleToGenerateNonNullRefreshToken() {
            // arrange
            // (setup in @BeforeEach)

            // act
            String token = jwtUtil.generateRefreshToken(userDetails);

            // assert
            assertThat(token).isNotBlank();
        }
    }

    @Nested
    @DisplayName("extractEmail")
    class ExtractEmail {

        @Test
        @DisplayName("should extract email from token")
        void shouldBeAbleToExtractEmailFromToken() {
            // arrange
            String token = jwtUtil.generateToken(userDetails);

            // act
            String email = jwtUtil.extractEmail(token);

            // assert
            assertThat(email).isEqualTo("user@test.com");
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("should return true for valid token and matching user")
        void shouldBeAbleToReturnTrueForValidTokenAndMatchingUser() {
            // arrange
            String token = jwtUtil.generateToken(userDetails);

            // act
            boolean valid = jwtUtil.validateToken(token, userDetails);

            // assert
            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("should return false when username does not match")
        void shouldBeAbleToReturnFalseWhenUsernameDoesNotMatch() {
            // arrange
            String token = jwtUtil.generateToken(userDetails);
            UserDetails otherUser = new User("other@test.com", "pass", List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));

            // act
            boolean valid = jwtUtil.validateToken(token, otherUser);

            // assert
            assertThat(valid).isFalse();
        }
    }

    @Nested
    @DisplayName("extractExpiration")
    class ExtractExpiration {

        @Test
        @DisplayName("should extract expiration date in future")
        void shouldBeAbleToExtractExpirationDateInFuture() {
            // arrange
            String token = jwtUtil.generateToken(userDetails);

            // act
            java.util.Date expiration = jwtUtil.extractExpiration(token);

            // assert
            assertThat(expiration).isAfter(new java.util.Date());
        }
    }
}
