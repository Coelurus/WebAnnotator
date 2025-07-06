package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import cz.cuni.mff.vopalenf.annotator.service.TeamService;
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
class TeamControllerTest {
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String FIRST_NAME = "test";
    private static final String LAST_NAME = "user";
    private static final String TEAM_NAME = "testteam";
    private static final String TEAM_NAME_2 = "testteam2";
    @MockBean
    TeamService teamService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @DisplayName("Should return all teams when requested by admin")
    void getTeams_ShouldReturnTeams_WhenRoleAdmin() throws Exception {
        when(teamService.getAllTeams())
                .thenReturn(List.of(
                        createTeam(TEAM_NAME),
                        createTeam(TEAM_NAME_2)
                ));
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(TEAM_NAME, TEAM_NAME_2)));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("Should return forbidden on get all users when requested by user")
    void getTeams_ShouldReturnForbidden_WhenRoleUser() throws Exception {
        mockMvc.perform(get("/api/teams")).andExpect(status().isForbidden());
    }

    public Team createTeam(String name) {
        return Team.builder().name(name).build();
    }

    public User createUser() {
        return User.builder().id(USER_ID).username(USERNAME).firstName(FIRST_NAME).lastName(LAST_NAME)
                .role(Role.ROLE_USER.getName()).build();

    }
}
