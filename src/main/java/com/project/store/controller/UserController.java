package com.project.store.controller;

import com.project.store.domain.user.request.UserRequest;
import com.project.store.domain.user.response.UserResponse;
import com.project.store.service.UserService;
import com.project.store.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping("/register/opn")
    public ResponseEntity <UserResponse> register(@Valid @RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.createUserService(userRequest);

        return new ResponseEntity <>(userResponse, HttpStatus.CREATED);
    }

}
