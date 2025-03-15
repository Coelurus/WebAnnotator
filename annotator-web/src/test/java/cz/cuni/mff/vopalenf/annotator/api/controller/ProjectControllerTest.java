package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
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
class ProjectControllerTest {

    private static final String PROJECT_NAME = "projectName";
    private static final String PROJECT_NAME_2 = "projectName2";

    @MockBean
    ProjectService projectService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Should return all projects")
    @WithMockUser(authorities = {"ROLE_USER"})
    void getTeam_ShouldReturnTeam_WhenAuthenticated() throws Exception {
        when(projectService.getAllProjects())
                .thenReturn(List.of(
                        createProject(PROJECT_NAME),
                        createProject(PROJECT_NAME_2)
                ));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].projectName").value(containsInAnyOrder(PROJECT_NAME, PROJECT_NAME_2)));
    }

    Project createProject(String name) {
        return Project.builder()
                .projectName(name)
                .build();
    }
}
