package com.logistics.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.auth.service.AuthService;
import com.logistics.common.dto.auth.AuthResponse;
import com.logistics.common.dto.auth.RegisterRequest;
import com.logistics.common.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // SecurityConfig requires a UserDetailsService bean; provide a mock so the web slice loads
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void register_validRequest_returns201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setRole(UserRole.ROLE_CUSTOMER);

        AuthResponse mockResponse = AuthResponse.builder()
            .accessToken("mock-access-token")
            .refreshToken("mock-refresh-token")
            .tokenType("Bearer")
            .expiresIn(3600)
            .user(AuthResponse.UserSummary.builder()
                .email("newuser@example.com")
                .fullName("Jane Smith")
                .role("ROLE_CUSTOMER")
                .build())
            .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Smith");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
