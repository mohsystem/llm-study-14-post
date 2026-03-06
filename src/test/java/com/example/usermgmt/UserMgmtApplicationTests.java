package com.example.usermgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserMgmtApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void authLifecycleAndPolicyValidation() throws Exception {
        mockMvc.perform(put("/api/admin/security/password-policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "minLength": 10,
                                  "requireUppercase": true,
                                  "requireLowercase": true,
                                  "requireDigit": true,
                                  "requireSpecial": false,
                                  "historyDepth": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minLength").value(10));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"john","email":"john@example.com","password":"short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"john","email":"john@example.com","password":"ValidPass12"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REGISTERED"))
                .andReturn().getResponse().getContentAsString();

        JsonNode regNode = objectMapper.readTree(registerResponse);
        assert regNode.get("userId").asLong() > 0;

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"john","password":"ValidPass12"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AUTHENTICATED"))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","currentPassword":"ValidPass12","newPassword":"AnotherPass34"}
                                """.formatted(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PASSWORD_CHANGED"));

        String resetTokenBody = mockMvc.perform(post("/api/auth/reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identity":"john@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESET_REQUESTED"))
                .andReturn().getResponse().getContentAsString();

        String resetToken = objectMapper.readTree(resetTokenBody).get("resetToken").asText();

        mockMvc.perform(post("/api/auth/reset-confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"resetToken":"%s","newPassword":"ThirdPass56"}
                                """.formatted(resetToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PASSWORD_RESET"));

        mockMvc.perform(post("/api/auth/recovery/encrypt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"recoverySecret":"sample-secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.encryptedValue").isNotEmpty());
    }
}
