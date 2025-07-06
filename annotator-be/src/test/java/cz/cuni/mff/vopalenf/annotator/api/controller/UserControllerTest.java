package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserControllerTest {

    private static final String USERNAME = "testuser";
    private static final String USERNAME_2 = "testuser2";
    private static final String FIRST_NAME = "test";
    private static final String LAST_NAME = "user";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @DisplayName("Should return all users when requested by admin")
    void getUsers_ShouldReturnUsers_WhenRoleAdmin() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of(
                        createUser(USERNAME),
                        createUser(USERNAME_2)
                ));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].username").value(containsInAnyOrder(USERNAME, USERNAME_2)));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("Should return forbidden on get all users when requested by user")
    void getUsers_ShouldReturnForbidden_WhenRoleUser() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].message").exists());
    }

    public User createUser(String username) {
        return User.builder().username(username).firstName(FIRST_NAME).lastName(LAST_NAME)
                .role(Role.ROLE_USER.getName()).build();
    }
}
