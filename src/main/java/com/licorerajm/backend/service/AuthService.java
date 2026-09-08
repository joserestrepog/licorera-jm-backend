package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.LoginRequest;
import com.licorerajm.backend.dto.LoginResponse;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario o contraseña incorrectos"
                        ));

        if (!user.getActive()) {
            throw new IllegalArgumentException("El usuario está inactivo");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "Usuario o contraseña incorrectos"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName()
        );
    }
}