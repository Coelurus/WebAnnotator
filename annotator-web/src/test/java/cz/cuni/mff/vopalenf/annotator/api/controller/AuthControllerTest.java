package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
    private static final String USERNAME = "testuser";
    private static final String FIRST_NAME = "test";
    private static final String LAST_NAME = "user";
    private static final String USERNAME_NON_EXISTING = "unknownuser";
    private static final String PASSWORD = "testpassword";
    private static final String PASSWORD_WRONG = "wrongpassword";

    private static final String CREDENTIALS_FORMAT = """
                {
                    "username": "%s",
                    "password": "%s"
                }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    void setup() {
        createUserEntity();
    }

    @Test
    @DisplayName("Login should succeed with valid credentials")
    void testLoginSuccess() throws Exception {
        String json = String.format(CREDENTIALS_FORMAT, USERNAME, PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Login should fail with invalid password")
    void testLoginFailInvalidPassword() throws Exception {
        String json = String.format(CREDENTIALS_FORMAT, USERNAME, PASSWORD_WRONG);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login should fail with non-existing username")
    void testLoginFailUserNotFound() throws Exception {
        String json = String.format(CREDENTIALS_FORMAT, USERNAME_NON_EXISTING, PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    public void createUserEntity() {
        {
            UserEntity user = UserEntity.builder()
                    .username(USERNAME)
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .role(Role.ROLE_USER)
                    .passwordHash(passwordEncoder.encode(PASSWORD))
                    .build();

            userRepository.save(user);
        }
    }
}
