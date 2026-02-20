package com.project.store.config.security;


import com.project.store.service.auth.AppUserDetailsService;
import com.project.store.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

/**
 * @author rahul
 * JwtRquest Filter
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {


    private static final List<String> PUBLIC_URLS = List.of(
            "/**/register/opn",
            "/auth/login/opn",

            // SWAGGER URLS
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    private final JwtUtil jwtUtil;

    private final AppUserDetailsService appUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        String jwt = null;
        String email = null;

        // check the authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

        }

        // if not found header, check cookies
        if (jwt == null) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // validate the token and set the security context

        if (jwt != null) {
            email = jwtUtil.extractEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
        }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/webjars")
                || path.startsWith("/configuration")
                || path.startsWith("/swagger")
                || path.contains("/register/opn")
                || path.contains("/login/opn");
    }

}