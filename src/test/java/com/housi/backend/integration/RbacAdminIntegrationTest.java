package com.housi.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.housi.backend.domain.model.Role;
import com.housi.backend.infrastructure.persistence.repository.RoleRepository;
import com.housi.backend.infrastructure.persistence.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class RbacAdminIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired StringRedisTemplate redis;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    final ObjectMapper objectMapper = new ObjectMapper();

    String adminToken;

    private static final java.util.Set<String> SEEDED_ROLES =
            java.util.Set.of("ROLE_ADMIN", "ROLE_USER");

    @BeforeEach
    void setUp() throws Exception {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
        userRepository.deleteAll();
        roleRepository
                .findAll()
                .forEach(
                        r -> {
                            if (!SEEDED_ROLES.contains(r.getName())) {
                                roleRepository.deleteById(r.getId());
                            }
                        });
        register("rbac-admin@example.com", "Pass12345");
        adminToken = loginAsAdmin();
    }

    @Test
    void roleCrudHappyPath() throws Exception {
        String roleName = "ROLE_MANAGER_" + System.nanoTime();

        MvcResult created =
                mockMvc.perform(
                                post("/api/v1/admin/roles")
                                        .header("Authorization", "Bearer " + adminToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"name\":\""
                                                        + roleName
                                                        + "\",\"description\":\"Manager\","
                                                        + "\"permissions\":[\"user:read\"]}"))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value(roleName))
                        .andExpect(
                                jsonPath(
                                        "$.permissions",
                                        org.hamcrest.Matchers.hasItem("user:read")))
                        .andReturn();

        String id =
                objectMapper
                        .readTree(created.getResponse().getContentAsString())
                        .get("id")
                        .asText();

        mockMvc.perform(get("/api/v1/admin/roles").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/admin/roles/" + id + "/permissions")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"permissions\":[\"user:read\",\"user:write\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions", org.hamcrest.Matchers.hasItem("user:write")));

        mockMvc.perform(
                        delete("/api/v1/admin/roles/" + id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void cannotDeleteSeededRole() throws Exception {
        Role admin = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        mockMvc.perform(
                        delete("/api/v1/admin/roles/" + admin.getId())
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void duplicateRoleNameRejected() throws Exception {
        String roleName = "ROLE_DUP_" + System.nanoTime();
        String body = "{\"name\":\"" + roleName + "\",\"description\":\"x\"}";

        mockMvc.perform(
                        post("/api/v1/admin/roles")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/v1/admin/roles")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void permissionCrudHappyPath() throws Exception {
        String permName = "feature:read-" + System.nanoTime();

        MvcResult created =
                mockMvc.perform(
                                post("/api/v1/admin/permissions")
                                        .header("Authorization", "Bearer " + adminToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"name\":\""
                                                        + permName
                                                        + "\",\"description\":\"x\"}"))
                        .andExpect(status().isCreated())
                        .andReturn();

        String id =
                objectMapper
                        .readTree(created.getResponse().getContentAsString())
                        .get("id")
                        .asText();

        mockMvc.perform(
                        delete("/api/v1/admin/permissions/" + id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void cannotDeleteSeededPermission() throws Exception {
        JsonNode perms =
                objectMapper.readTree(
                        mockMvc.perform(
                                        get("/api/v1/admin/permissions")
                                                .header("Authorization", "Bearer " + adminToken))
                                .andReturn()
                                .getResponse()
                                .getContentAsString());
        String userReadId = null;
        for (JsonNode p : perms) {
            if ("user:read".equals(p.get("name").asText())) {
                userReadId = p.get("id").asText();
                break;
            }
        }
        org.assertj.core.api.Assertions.assertThat(userReadId).isNotNull();

        mockMvc.perform(
                        delete("/api/v1/admin/permissions/" + userReadId)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void assignUserRolesPromotesUser() throws Exception {
        String email = "promotable-" + System.nanoTime() + "@example.com";
        register(email, "Pass12345");

        String userId = userRepository.findByEmail(email).orElseThrow().getId().toString();

        // create a custom role
        String roleName = "ROLE_AUDITOR_" + System.nanoTime();
        mockMvc.perform(
                        post("/api/v1/admin/roles")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"name\":\""
                                                + roleName
                                                + "\",\"description\":\"x\","
                                                + "\"permissions\":[\"user:read\"]}"))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/api/v1/admin/users/" + userId + "/roles")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"roles\":[\"ROLE_USER\",\"" + roleName + "\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", org.hamcrest.Matchers.hasItem(roleName)));
    }

    @Test
    void cannotRemoveLastAdmin() throws Exception {
        String adminUserId =
                userRepository
                        .findByEmail("rbac-admin@example.com")
                        .orElseThrow()
                        .getId()
                        .toString();

        mockMvc.perform(
                        put("/api/v1/admin/users/" + adminUserId + "/roles")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"roles\":[\"ROLE_USER\"]}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rolesEndpointFetchesPermissionsWithoutLazyError() throws Exception {
        mockMvc.perform(get("/api/v1/admin/roles").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].permissions").isArray());
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
                                                "{\"email\":\"rbac-admin@example.com\","
                                                        + "\"password\":\"Pass12345\"}"))
                        .andExpect(status().isOk())
                        .andReturn();
        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("token")
                .asText();
    }
}
