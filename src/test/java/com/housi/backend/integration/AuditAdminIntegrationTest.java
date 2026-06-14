package com.housi.backend.integration;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.housi.backend.TestcontainersConfiguration;
import com.housi.backend.repository.AuditRepository;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuditAdminIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired StringRedisTemplate redis;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired AuditRepository auditRepository;

    final ObjectMapper objectMapper = new ObjectMapper();

    private static final java.util.Set<String> SEEDED_ROLES =
            java.util.Set.of("ROLE_ADMIN", "ROLE_USER");

    String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
        auditRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository
                .findAll()
                .forEach(
                        r -> {
                            if (!SEEDED_ROLES.contains(r.getName())) {
                                roleRepository.delete(r);
                            }
                        });
        register("audit-admin@example.com", "Pass12345");
        adminToken = loginAsAdmin();
    }

    @Test
    void listAuditsRequiresAdminRead() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audits")).andExpect(status().isUnauthorized());
    }

    @Test
    void listAuditsReturnsPaginatedResults() throws Exception {
        String roleName = "ROLE_AUDITABLE_" + System.nanoTime();
        mockMvc.perform(
                        post("/api/v1/admin/roles")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"name\":\""
                                                + roleName
                                                + "\",\"description\":\"Auditable\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/admin/audits").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.links").exists())
                .andExpect(
                        jsonPath("$.meta.totalItems").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data[0].objEntity").value("Role"))
                .andExpect(jsonPath("$.data[0].action").value("created"))
                .andExpect(jsonPath("$.data[0].actorId").isNotEmpty());
    }

    @Test
    void auditEntryCreatedOnUserCreate() throws Exception {
        mockMvc.perform(
                        post("/api/v1/admin/users")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"firstName\":\"John\",\"lastName\":\"Doe\","
                                                + "\"email\":\"john.doe@example.com\","
                                                + "\"password\":\"Pass12345\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/admin/audits").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data[?(@.objEntity == 'User' && @.action == 'created')]")
                                .isArray());
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

    private String loginAsAdmin() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"email\":\"audit-admin@example.com\","
                                                        + "\"password\":\"Pass12345\"}"))
                        .andExpect(status().isOk())
                        .andReturn();
        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("token")
                .asText();
    }
}
