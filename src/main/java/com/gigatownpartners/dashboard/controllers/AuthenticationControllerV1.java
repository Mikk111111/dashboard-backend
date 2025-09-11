package com.gigatownpartners.dashboard.controllers;

import com.gigatownpartners.dashboard.dtos.LoginUserDto;
import com.gigatownpartners.dashboard.dtos.RegisterUserDto;
import com.gigatownpartners.dashboard.dtos.UpdatePasswordUserDto;
import com.gigatownpartners.dashboard.entities.User;
import com.gigatownpartners.dashboard.exceptions.InvalidRefreshTokenException;
import com.gigatownpartners.dashboard.services.AuthenticationService;
import com.gigatownpartners.dashboard.services.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationControllerV1 {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    public AuthenticationControllerV1(JwtService jwtService, AuthenticationService authenticationService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@RequestBody RegisterUserDto registerUserDto) {
        authenticationService.signup(registerUserDto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset")
    public ResponseEntity<Void> reset(@RequestBody UpdatePasswordUserDto updatePasswordUserDto) {
        authenticationService.changePassword(updatePasswordUserDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue("refresh_token") String refreshToken) {
        if (jwtService.isRefreshTokenExpired(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token not valid. Please login.");
        }

        String email = jwtService.extractRefreshUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String jwtToken = jwtService.generateToken(userDetails);
        String jwtRefreshToken = jwtService.generateRefreshToken(userDetails);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", jwtRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        httpHeaders.add("Authorization", "Bearer " + jwtToken);

        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String jwtRefreshToken = jwtService.generateRefreshToken(authenticatedUser);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", jwtRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        httpHeaders.add("Authorization", "Bearer " + jwtToken);

        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
