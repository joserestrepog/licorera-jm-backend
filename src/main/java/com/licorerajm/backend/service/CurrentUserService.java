package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.CurrentUserResponse;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CurrentUserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "No hay un usuario autenticado"
            );
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "El usuario autenticado no fue encontrado"
                        )
                );

        return new CurrentUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRole().getName(),
                user.getActive()
        );
    }
}