package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.role
            WHERE u.username = :username
            """)
    Optional<User> findByUsername(String username);
}