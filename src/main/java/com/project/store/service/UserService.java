package com.project.store.service;

import com.project.store.domain.user.request.UserRequest;
import com.project.store.domain.user.response.UserResponse;

public interface UserService {


    UserResponse createAdminUserService(UserRequest userRequest);

    UserResponse createUserService(UserRequest userRequest);

    UserResponse getUserByEmailIdService(String email);

}
