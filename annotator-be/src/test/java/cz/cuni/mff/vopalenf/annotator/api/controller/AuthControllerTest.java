package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.config.UserAuthProvider;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadCredentialsException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    private static final String TOKEN = "testtoken";
    private static final String INVALID_CREDENTIALS_MSG = "Invalid credentials";
    private static final String UNKNOWN_USER_MSG = "Unknown user";
    private static final String USERNAME_ALREADY_EXISTS_MSG = "Username already exists";

    private static final String LOGIN_CREDENTIALS_FORMAT = """
                {
                    "username": "%s",
                    "password": "%s"
                }
            """;

    private static final String SIGNUP_CREDENTIALS_FORMAT = """
                {
                    "username": "%s",
                    "password": "%s",
                    "firstName": "%s",
                    "lastName": "%s"
                }
            """;

    private static final String INVALID_CREDENTIALS = """
                {
                    "username": "aaaa"
                }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthProvider userAuthProvider;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Login should succeed with valid credentials")
    void login_ShouldReturnUser_WhenValidCredentials() throws Exception {
        String json = String.format(LOGIN_CREDENTIALS_FORMAT, USERNAME, PASSWORD);

        User user = createUser();

        when(userService.login(any())).thenReturn(user);
        when(userAuthProvider.createToken(user)).thenReturn(TOKEN);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TOKEN));
    }

    @Test
    @DisplayName("Login should fail with invalid password")
    void login_ShouldReturnBadCredentials_WhenInvalidPassword() throws Exception {
        String json = String.format(LOGIN_CREDENTIALS_FORMAT, USERNAME, PASSWORD_WRONG);

        when(userService.login(any()))
                .thenThrow(new BadCredentialsException(INVALID_CREDENTIALS_MSG, UserService.class.getSimpleName()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value(INVALID_CREDENTIALS_MSG));
    }

    @Test
    @DisplayName("Login should fail with non-existing username")
    void login_ShouldReturnNotFound_WhenUsernameNotExists() throws Exception {
        String json = String.format(LOGIN_CREDENTIALS_FORMAT, USERNAME_NON_EXISTING, PASSWORD);

        when(userService.login(any()))
                .thenThrow(new NotFoundException(UNKNOWN_USER_MSG, UserService.class.getSimpleName()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message").value(UNKNOWN_USER_MSG));
    }

    @Test
    @DisplayName("Signup should succeed with unique username")
    void signup_ShouldReturnUser_WhenValidCredentials() throws Exception {
        String json = String.format(SIGNUP_CREDENTIALS_FORMAT, USERNAME, PASSWORD, FIRST_NAME, LAST_NAME);

        User user = createUser();

        when(userService.signup(any())).thenReturn(user);
        when(userAuthProvider.createToken(user)).thenReturn(TOKEN);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TOKEN));
    }


    @Test
    @DisplayName("Signup should fail with non-unique username")
    void signup_ShouldReturnBadCredentials_WhenUsernameExists() throws Exception {
        String json = String.format(SIGNUP_CREDENTIALS_FORMAT, USERNAME, PASSWORD, FIRST_NAME, LAST_NAME);

        when(userService.signup(any()))
                .thenThrow(new BadCredentialsException(USERNAME_ALREADY_EXISTS_MSG, UserService.class.getSimpleName()));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value(USERNAME_ALREADY_EXISTS_MSG));
    }

    @Test
    @DisplayName("Login should fail with missing password")
    void login_ShouldReturnBadRequest_WhenMissingPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_CREDENTIALS))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").exists());
    }

    public User createUser() {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(Role.ROLE_USER.getName())
                .build();
    }
}
