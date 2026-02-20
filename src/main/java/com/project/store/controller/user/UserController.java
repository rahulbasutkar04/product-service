package com.project.store.controller.user;

import com.project.store.domain.user.request.UserRequest;
import com.project.store.domain.user.response.UserResponse;
import com.project.store.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author rahul
 * User Controlller
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    /**
     * Register User
     *
     * @param userRequest {@link UserRequest}
     * @return {@link UserResponse}
     */
    @PostMapping("/register/opn")
    public ResponseEntity <UserResponse> register(@Valid @RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.createUserService(userRequest);

        return new ResponseEntity <>(userResponse, HttpStatus.CREATED);
    }

    /**
     * Get User Profile [USER]
     *
     * @param authentication {@link Authentication}
     * @return {@link UserResponse}
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myProfile/secure")
    public ResponseEntity <UserResponse> getMyProfile(Authentication authentication) {

        if (authentication != null || authentication.isAuthenticated()) {

            String email = authentication.getName();

            UserResponse userResponse = userService.getUserByEmailIdService(email);

            return new ResponseEntity <>(userResponse, HttpStatus.OK);
        }

        return new ResponseEntity <>(HttpStatus.UNAUTHORIZED);


    }

}
