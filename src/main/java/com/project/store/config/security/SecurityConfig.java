package com.project.store.config.security;

import com.project.store.config.handler.CustomAccessDeniedHandler;
import com.project.store.config.handler.CustomAuthenticationEntryPoint;
import com.project.store.service.auth.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author rahul
 * Security config class
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        //  SWAGGER ENDPOINTS – permit by path prefix so /v3/api-docs and /v3/api-docs/swagger-config are allowed
                        .requestMatchers(swaggerRequestMatcher()).permitAll()

                        // PUBLIC REGISTER APIs
                        .requestMatchers(
                                "/**/register/opn",
                                "/auth/login/opn"
                        ).permitAll()

                        // Everything Else Secured
                        .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                // JWT Filter MUST be added
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    /**
     * Matcher for Swagger/OpenAPI docs and UI so they are always permitted (no 401 on fetch).
     */
    private static RequestMatcher swaggerRequestMatcher() {
        return request -> {
            String path = request.getRequestURI();
            return path.startsWith("/v3/api-docs")
                    || path.startsWith("/swagger-ui")
                    || path.equals("/swagger-ui.html")
                    || path.startsWith("/swagger-resources")
                    || path.startsWith("/webjars")
                    || path.startsWith("/configuration");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }


}
