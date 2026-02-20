package com.project.store.service.auth;

import com.project.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author rahul
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Override  method for user fetch
     *
     * @param email {@link String}
     * @return {@link UserDetails}
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        com.project.store.domain.user.User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found for:" + email));

        ArrayList <SimpleGrantedAuthority> authorities = new ArrayList <>();

        authorities.add(new SimpleGrantedAuthority(existingUser.getRole().name()));

        return new User(existingUser.getEmail(), existingUser.getPassword(), authorities);

    }

}
