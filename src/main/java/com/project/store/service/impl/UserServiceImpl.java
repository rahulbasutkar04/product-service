package com.project.store.service.impl;

import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import com.project.store.domain.user.request.UserRequest;
import com.project.store.domain.user.response.UserResponse;
import com.project.store.exception.ErrorResponseEnum;
import com.project.store.exception.ValidationError;
import com.project.store.exception.ValidationErrorType;
import com.project.store.exception.ValidationException;
import com.project.store.repository.UserRepository;
import com.project.store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOG= LoggerFactory.getLogger(UserServiceImpl.class);


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponse createAdminUserService(UserRequest userRequest) {

        try {

            Optional <User> existingUser = userRepository.findByEmail(userRequest.getEmail());

            if (existingUser.isPresent()) {
                throw new ValidationException(
                        new ValidationError(
                                "User already exists. Abort!",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()
                        ),
                        ErrorResponseEnum.USER_CONFLICT
                );
            }

            User user = User.builder()
                    .email(userRequest.getEmail())
                    .role(UserRole.ROLE_ADMIN)
                    .contact(userRequest.getContact())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .name(userRequest.getName())
                    .build();

            User savedUser = userRepository.save(user);

            return UserResponse.buildUserResponseFromUserEntity(savedUser);

        } catch (DataAccessException ex) {

            LOG.error("Error while creating admin user inside 'createAdminUserService'");

            throw new ValidationException(
                    new ValidationError(
                            "Failed to register Admin User, please contact administrator!",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()
                    ),
                    ErrorResponseEnum.UNPROCESSABLE_ENTITY
            );
        }
    }


    @Override
    public UserResponse createUserService(UserRequest userRequest) {

        try {

            // Step 1 — Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());

            if (existingUser.isPresent()) {
                throw new ValidationException(
                        new ValidationError(
                                "User already exists. Abort!",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()
                        ),
                        ErrorResponseEnum.USER_CONFLICT
                );
            }

            // Step 2 — Create User
            User user = User.builder()
                    .email(userRequest.getEmail())
                    .role(UserRole.ROLE_USER)
                    .contact(userRequest.getContact())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .name(userRequest.getName())
                    .build();

            // Step 3 — Save User
            User savedUser = userRepository.save(user);

            // Step 4 — Return Response
            return UserResponse.buildUserResponseFromUserEntity(savedUser);

        } catch (DataAccessException ex) {

            LOG.error("Error while creating admin user inside 'createUserService'");

            throw new ValidationException(
                    new ValidationError(
                            "Failed to register User, please contact administrator!",
                            ValidationErrorType.UNPROCESSABLE.getErrorType()
                    ),
                    ErrorResponseEnum.UNPROCESSABLE_ENTITY
            );
        }
    }

    @Override
    public UserResponse getUserByEmailIdService(String email) {

        try {
            if (! StringUtils.hasLength(email)) {
                throw new ValidationException(ValidationError.builder()
                        .errorMessage("Email can not be empty or null, abort!")
                        .build(), ErrorResponseEnum.VALIDATION_ERROR);
            }

            Optional <User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty() || existingUser == null) {
                throw new ValidationException(
                        new ValidationError(
                                "User Not found, Abort!",
                                ValidationErrorType.INVALID_REQUEST.getErrorType()
                        ),
                        ErrorResponseEnum.UNPROCESSABLE_ENTITY
                );
            }

            return UserResponse.buildUserResponseFromUserEntity(existingUser.get());

        } catch (ValidationException validationException) {
            throw validationException;
        } catch (Exception e) {

            LOG.error("Unable to fetch user inside 'getUserByEmailIdService', abort!");
            throw e;
        }
    }


}
