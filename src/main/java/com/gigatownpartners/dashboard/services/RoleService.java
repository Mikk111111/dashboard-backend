package com.gigatownpartners.dashboard.services;

import com.gigatownpartners.dashboard.dtos.RoleDto;
import com.gigatownpartners.dashboard.entities.Role;
import com.gigatownpartners.dashboard.exceptions.EmailExistsException;
import com.gigatownpartners.dashboard.repositories.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void createRole(RoleDto roleDto) {
        Role role = new Role(roleDto.getName());

        try {
            roleRepository.save(role);
        } catch (DataIntegrityViolationException _) {
            throw new EmailExistsException("The role " + role.getName() + " is already added.");
        }
    }
}
