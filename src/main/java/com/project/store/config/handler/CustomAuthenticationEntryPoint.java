package com.project.store.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Throwable exception = (Throwable) request.getAttribute("jakarta.servlet.error.exception");

        if (exception instanceof MethodArgumentNotValidException) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "message": "Invalid input provided",
                    "status": 400
                }
                """);

            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
            {
                "message": "User is not authenticated",
                "status": 401
            }
            """);
    }

}