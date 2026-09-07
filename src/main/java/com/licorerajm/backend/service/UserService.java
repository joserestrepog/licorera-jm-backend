package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.UserRequest;
import com.licorerajm.backend.dto.UserResponse;
import com.licorerajm.backend.entity.Role;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.RoleRepository;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserResponse> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El usuario no fue encontrado"));

        return toResponse(user);
    }

    public UserResponse create(UserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Ya existe un usuario con ese nombre de usuario");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El rol no fue encontrado"));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());

        // Temporalmente guardamos el valor recibido.
        // Posteriormente implementaremos el hash de contraseña.
        user.setPasswordHash(request.getPassword());

        user.setRole(role);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public UserResponse update(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El usuario no fue encontrado"));

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new DuplicateResourceException(
                    "Ya existe un usuario con ese nombre de usuario");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El rol no fue encontrado"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());

        // Temporalmente guardamos el valor recibido.
        // Posteriormente implementaremos el hash de contraseña.
        user.setPasswordHash(request.getPassword());

        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public UserResponse deactivate(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El usuario no fue encontrado"));

        user.setActive(false);

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRole().getId(),
                user.getRole().getName(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}