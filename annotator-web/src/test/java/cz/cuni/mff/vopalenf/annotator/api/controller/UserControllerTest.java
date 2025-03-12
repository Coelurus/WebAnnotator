package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.config.UserAuthProvider;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String FIRST_NAME = "test";
    private static final String LAST_NAME = "user";
    private static final String PASSWORD = "testpassword";
    private static final String TEAM_NAME = "testteam";

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BEARER_TOKEN_ADMIN_PART = "adminToken";
    private static final String BEARER_TOKEN_USER_PART = "userToken";
    @MockBean
    UserAuthProvider userAuthProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    void setup() {
        Mockito.when(userAuthProvider.validate(BEARER_TOKEN_ADMIN_PART)).thenReturn(
                new UsernamePasswordAuthenticationToken(USERNAME, null, Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())))
        );
        Mockito.when(userAuthProvider.validate(BEARER_TOKEN_USER_PART)).thenReturn(
                new UsernamePasswordAuthenticationToken(USERNAME, null, Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_USER.name())))
        );
    }

    @Test
    @DisplayName("Should return all users when requested by admin")
    void testGetAllUsersSucceedsWhenAdmin() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", BEARER_PREFIX + BEARER_TOKEN_ADMIN_PART))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].username").value(containsInAnyOrder("admin", "user", "user1")));
    }

    @Test
    @DisplayName("Should return 403 on get all teams when requested by user")
    void testGetAllUsersFailsWhenUser() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", BEARER_PREFIX + BEARER_TOKEN_USER_PART))
                .andExpect(status().isForbidden());
    }

}
