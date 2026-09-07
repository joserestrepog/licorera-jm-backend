package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.RoleRequest;
import com.licorerajm.backend.dto.RoleResponse;
import com.licorerajm.backend.entity.Role;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoleResponse findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El rol no fue encontrado"));

        return toResponse(role);
    }

    public RoleResponse create(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Ya existe un rol con ese nombre");
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setActive(true);

        Role savedRole = roleRepository.save(role);

        return toResponse(savedRole);
    }

    public RoleResponse update(Long id, RoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El rol no fue encontrado"));

        if (!role.getName().equals(request.getName())
                && roleRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException(
                    "Ya existe un rol con ese nombre");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Role updatedRole = roleRepository.save(role);

        return toResponse(updatedRole);
    }

    public RoleResponse deactivate(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El rol no fue encontrado"));

        role.setActive(false);

        Role updatedRole = roleRepository.save(role);

        return toResponse(updatedRole);
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getActive()
        );
    }
}