package com.project.store.service.auth;

import com.project.store.domain.user.auth.response.AuthResponse;
import com.project.store.domain.user.auth.request.AuthRequest;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationError;
import com.project.store.exception.ValidationErrorType;
import com.project.store.exception.ValidationException;
import com.project.store.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author rahul
 */
@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;


    /**
     * Login User  [ADMIN & USER]
     *
     * @param authRequest {@link AuthRequest}
     * @return {@link AuthResponse}
     */
    public AuthResponse loginService(AuthRequest authRequest) {

        try {

            authenticate(authRequest.getEmail(), authRequest.getPassword());

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(authRequest.getEmail());


            final String accessToken = jwtUtil.generateToken(userDetails);
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails);


            return AuthResponse.builder()
                    .token(accessToken)
                    .email(authRequest.getEmail())
                    .refreshToken(refreshToken)
                    .build();


        } catch (BadCredentialsException ex) {

            throw new ValidationException(
                    new ValidationError(
                            "Email or password is incorrect",
                            ValidationErrorType.INVALID_REQUEST.getErrorType()
                    ),
                    ErrorResponseEnum.INVALID_REQUEST
            );

        } catch (DisabledException ex) {

            throw new ValidationException(
                    new ValidationError(
                            "Account is disabled",
                            ValidationErrorType.UNAUTHORIZED.getErrorType()
                    ),
                    ErrorResponseEnum.UNAUTHORIZED
            );

        } catch (Exception ex) {

            throw new ValidationException(
                    new ValidationError(
                            "Authentication Failed",
                            ValidationErrorType.UNAUTHORIZED.getErrorType()
                    ),
                    ErrorResponseEnum.UNAUTHORIZED
            );
        }
    }

    private void authenticate(String email, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

}
