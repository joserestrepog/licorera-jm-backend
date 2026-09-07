package com.licorerajm.backend.controller;

import com.licorerajm.backend.dto.UserRequest;
import com.licorerajm.backend.dto.UserResponse;
import com.licorerajm.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity.ok(
                userService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.deactivate(id)
        );
    }
}