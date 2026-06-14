package com.housi.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.housi.backend.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthSecurityIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired StringRedisTemplate redis;
    @Autowired com.housi.backend.repository.UserRepository userRepository;

    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetState() {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
        userRepository.deleteAll();
    }

    @Test
    void registerLoginAndCurrentUser() throws Exception {
        String email = "auth-flow-" + System.nanoTime() + "@example.com";

        register(email, "Pass12345");

        String token = login(email, "Pass12345");
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/v1/users/current").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.roles", org.hamcrest.Matchers.hasItem("ROLE_USER")));
    }

    @Test
    void missingTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/current")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        mockMvc.perform(
                        get("/api/v1/users/current")
                                .header("Authorization", "Bearer bad.jwt.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void badCredentialsReturn401() throws Exception {
        String email = "bad-creds-" + System.nanoTime() + "@example.com";
        register(email, "Pass12345");

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"" + email + "\",\"password\":\"WrongPass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fiveFailedLoginsTriggerLockout() throws Exception {
        String email = "lockout-" + System.nanoTime() + "@example.com";
        register(email, "Pass12345");

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            "{\"email\":\""
                                                    + email
                                                    + "\",\"password\":\"WrongPass\"}"))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"" + email + "\",\"password\":\"Pass12345\"}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void nonAdminCannotAccessAdminRoutes() throws Exception {
        // First admin (first user)
        String adminEmail = "admin-" + System.nanoTime() + "@example.com";
        register(adminEmail, "Pass12345");

        // Second user (regular)
        String userEmail = "user-" + System.nanoTime() + "@example.com";
        register(userEmail, "Pass12345");

        String userToken = login(userEmail, "Pass12345");

        mockMvc.perform(get("/api/v1/admin").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        delete("/api/v1/admin/" + java.util.UUID.randomUUID())
                                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private void register(String email, String password) throws Exception {
        String body =
                "{\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\""
                        + email
                        + "\",\"password\":\""
                        + password
                        + "\"}";
        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"email\":\""
                                                        + email
                                                        + "\",\"password\":\""
                                                        + password
                                                        + "\"}"))
                        .andExpect(status().isOk())
                        .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }
}
