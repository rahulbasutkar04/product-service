package com.project.store.controller.auth;

import com.project.store.domain.user.auth.response.AuthResponse;
import com.project.store.domain.user.auth.request.AuthRequest;
import com.project.store.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login/opn")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {

        AuthResponse response = authService.loginService(authRequest);

        return ResponseEntity.ok(response);
    }

}
