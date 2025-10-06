package com.gigatownpartners.dashboard.services;

import com.gigatownpartners.dashboard.dtos.LoginUserDto;
import com.gigatownpartners.dashboard.dtos.RegisterUserDto;
import com.gigatownpartners.dashboard.dtos.UpdatePasswordUserDto;
import com.gigatownpartners.dashboard.entities.Role;
import com.gigatownpartners.dashboard.entities.User;
import com.gigatownpartners.dashboard.exceptions.EmailExistsException;
import com.gigatownpartners.dashboard.repositories.RoleRepository;
import com.gigatownpartners.dashboard.repositories.UserRepository;
import com.gigatownpartners.dashboard.utils.PasswordValidator;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(RegisterUserDto input) {
        if (!PasswordValidator.isValid(input.getPassword())) {
            throw new BadCredentialsException("Password must be at least 8 characters long, include uppercase, lowercase, a digit, and a special character.");
        }

        User user = new User(input.getFullName(), input.getEmail(), passwordEncoder.encode(input.getPassword()), passwordExpiration());
        Role role = roleRepository.findByName("USER").orElseThrow();
        user.getRoles().add(role);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException _) {
            throw new EmailExistsException("The email " + input.getEmail() + " is already registered.");
        }
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
