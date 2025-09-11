package com.gigatownpartners.dashboard.controllers;

import com.gigatownpartners.dashboard.dtos.RoleDto;
import com.gigatownpartners.dashboard.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/roles")
@RestController
public class RoleControllerV1 {
    private final RoleService roleService;

    public RoleControllerV1(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createRole(@RequestBody RoleDto roleDto) {
        roleService.createRole(roleDto);

        return ResponseEntity.ok().build();
    }
}
