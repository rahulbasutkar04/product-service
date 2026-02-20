package com.project.store.repository;

import com.project.store.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Repository class for {@link User}
 */
@Repository
public interface UserRepository extends JpaRepository <User, Long> {


    Optional <User> findByEmail(String email);


}
