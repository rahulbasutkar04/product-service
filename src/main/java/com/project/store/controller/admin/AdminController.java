package com.project.store.controller.admin;

import com.project.store.domain.user.request.UserRequest;
import com.project.store.domain.user.response.UserResponse;
import com.project.store.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author rahul
 * Admin Controller
 */
@RestController
@RequestMapping("/admin")
public class AdminController {


    @Value("${admin.register.secret}")
    private String adminRegisterSecret;

    @Autowired
    private UserService userService;


    /**
     * Secure API to Register the admin user
     *
     * @param userRequest {@link UserRequest}
     * @return {@link UserResponse}
     */
    @PostMapping("/register/opn")
    public ResponseEntity<UserResponse> registerAdmin(
            @RequestHeader("X-ADMIN-SECRET") String adminSecret,
            @Valid @RequestBody UserRequest userRequest) {

        if(adminSecret.equals(adminRegisterSecret))
        {
            UserResponse response =
                    userService.createAdminUserService(userRequest);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        return new ResponseEntity <>(HttpStatus.UNAUTHORIZED);

    }


}
