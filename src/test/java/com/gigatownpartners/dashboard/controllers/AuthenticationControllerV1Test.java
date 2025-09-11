package com.gigatownpartners.dashboard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigatownpartners.dashboard.dtos.LoginUserDto;
import com.gigatownpartners.dashboard.dtos.RegisterUserDto;
import com.gigatownpartners.dashboard.dtos.UpdatePasswordUserDto;
import com.gigatownpartners.dashboard.entities.User;
import com.gigatownpartners.dashboard.exceptions.GlobalExceptionHandler;
import com.gigatownpartners.dashboard.services.AuthenticationService;
import com.gigatownpartners.dashboard.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.Cookie;

import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
public class AuthenticationControllerV1Test {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationControllerV1 controller;
    
    private MockMvc mockMvc;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testSignup() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setFullName("John Doe");
        registerUserDto.setEmail("john@example.com");
        registerUserDto.setPassword("Password123!");

        doNothing().when(authenticationService).signup(any(RegisterUserDto.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerUserDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testReset() throws Exception {
        UpdatePasswordUserDto updatePasswordUserDto = new UpdatePasswordUserDto();
        updatePasswordUserDto.setEmail("john@example.com");
        updatePasswordUserDto.setOldPassword("OldPassword123!");
        updatePasswordUserDto.setNewPassword("NewPassword123!");

        doNothing().when(authenticationService).changePassword(any(UpdatePasswordUserDto.class));

        mockMvc.perform(put("/api/v1/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatePasswordUserDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRefreshExpiredToken() throws Exception {
        String expiredToken = "expiredToken";
        when(jwtService.isRefreshTokenExpired(expiredToken)).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", expiredToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Refresh token not valid. Please login."))
                .andExpect(jsonPath("$.description").value("The JWT refresh token has expired"));
    }

    @Test
    public void testRefreshValidToken() throws Exception {
        String validRefreshToken = "validRefreshToken";
        when(jwtService.isRefreshTokenExpired(validRefreshToken)).thenReturn(false);
        when(jwtService.extractRefreshUsername(validRefreshToken)).thenReturn("john@example.com");

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("john@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);

        String accessToken = "accessToken123";
        String newRefreshToken = "newRefreshToken123";
        when(jwtService.generateToken(userDetails)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(userDetails)).thenReturn(newRefreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", validRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=" + newRefreshToken)))
                .andExpect(header().string("Authorization", "Bearer " + accessToken));
    }

    @Test
    public void testLogin() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("john@example.com");
        loginUserDto.setPassword("Password123!");

        User user = new User("John Doe", "john@example.com", "encodedPassword", new Date());
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("accessToken123");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=refreshToken123")))
                .andExpect(header().string("Authorization", "Bearer " + "accessToken123"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}