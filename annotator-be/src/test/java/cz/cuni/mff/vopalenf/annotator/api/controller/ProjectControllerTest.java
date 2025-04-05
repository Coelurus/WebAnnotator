package cz.cuni.mff.vopalenf.annotator.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.request.ProjectRequest;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectControllerTest {

    private static final Long PROJECT_ID = 1L;
    private static final String PROJECT_NAME = "projectName";
    private static final String PROJECT_NAME_2 = "projectName2";
    private static final String PRIORITY = "HIGH";
    private static final LocalDate DUE_DATE = LocalDate.now();

    private static final String NOT_FOUND_MESSAGE = "Project not found";
    private static final String SCOPE = "project";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockBean
    ProjectService projectService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Should return all projects")
    @WithMockUser(authorities = {"ROLE_USER"})
    void getAllProjects_ShouldReturnProjects_WhenAuthenticated() throws Exception {
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

    @Test
    @DisplayName("Should return project by ID")
    @WithMockUser(authorities = {"ROLE_USER"})
    void getProjectById_ShouldReturnProject_WhenAuthenticated() throws Exception {
        Project project = createProject(PROJECT_NAME);
        when(projectService.getProject(PROJECT_ID)).thenReturn(project);

        mockMvc.perform(get("/api/projects/{id}", PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value(PROJECT_NAME));
    }

    @Test
    @DisplayName("Should return 404 when project not found by ID")
    @WithMockUser(authorities = {"ROLE_USER"})
    void getProjectById_ShouldReturn404_WhenProjectNotFound() throws Exception {
        doThrow(new NotFoundException(NOT_FOUND_MESSAGE, SCOPE)).when(projectService).getProject(PROJECT_ID);

        mockMvc.perform(get("/api/projects/{id}", PROJECT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete project by ID")
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteProjectById_ShouldDeleteProject_WhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", PROJECT_ID))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 when deleting project not found by ID")
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteProjectById_ShouldReturn404_WhenProjectNotFound() throws Exception {
        doThrow(new NotFoundException(NOT_FOUND_MESSAGE, SCOPE)).when(projectService).deleteProject(PROJECT_ID);

        mockMvc.perform(delete("/api/projects/{id}", PROJECT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update project by ID")
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateProject_ShouldUpdateProject_WhenAuthenticated() throws Exception {
        ProjectRequest projectRequest = createProjectRequest();
        Project updatedProject = createProject(PROJECT_NAME);
        when(projectService.updateProject(PROJECT_ID, projectRequest)).thenReturn(updatedProject);

        mockMvc.perform(put("/api/projects/{projectId}", PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value(PROJECT_NAME));
    }

    @Test
    @DisplayName("Should return 404 when updating project not found by ID")
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateProject_ShouldReturn404_WhenProjectNotFound() throws Exception {
        ProjectRequest projectRequest = createProjectRequest();
        when(projectService.updateProject(PROJECT_ID, projectRequest)).thenThrow(new NotFoundException(NOT_FOUND_MESSAGE, SCOPE));

        mockMvc.perform(put("/api/projects/{projectId}", PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isNotFound());
    }

    Project createProject(String name) {
        return Project.builder()
                .projectName(name)
                .build();
    }

    ProjectRequest createProjectRequest() {
        return ProjectRequest.builder()
                .projectName(PROJECT_NAME)
                .deadline(DUE_DATE)
                .priority(PRIORITY)
                .build();
    }
}
