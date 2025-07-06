package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.FrameCount;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.service.FileSystemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class FileSystemControllerTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long PROJECT_ID_OUT_OF_RANGE = 999L;
    private static final Integer POSITION = 5;
    private static final Integer POSITION_OUT_OF_RANGE = 999;
    private static final String NOT_FOUND_MSG = "Not found";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileSystemService fileSystemService;

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("Should return image when frame exists")
    void getFrame_ShouldReturnImage_WhenFrameExists() throws Exception {
        Resource mockResource = new ByteArrayResource(new byte[]{1, 2, 3});

        when(fileSystemService.getFrame(PROJECT_ID, POSITION)).thenReturn(mockResource);

        mockMvc.perform(
                get("/api/projects/{projectId}/frame/{position}", PROJECT_ID, POSITION).accept(MediaType.IMAGE_JPEG))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("Should return not found when frame does not exist")
    void getFrame_ShouldReturnNotFound_WhenFrameDoesNotExist() throws Exception {
        when(fileSystemService.getFrame(PROJECT_ID, POSITION_OUT_OF_RANGE))
                .thenThrow(new NotFoundException(NOT_FOUND_MSG, FileSystemService.class.getSimpleName()));

        mockMvc.perform(get("/api/projects/{projectId}/frame/{position}", PROJECT_ID, POSITION_OUT_OF_RANGE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message").value(NOT_FOUND_MSG));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getFramesCount_ShouldReturnFrameCount_WhenProjectExists() throws Exception {
        FrameCount frameCount = new FrameCount(10);

        when(fileSystemService.getFramesCount(PROJECT_ID)).thenReturn(frameCount);

        mockMvc.perform(get("/api/projects/{id}/frame/count", PROJECT_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getFramesCount_ShouldReturnNotFound_WhenProjectDoesNotExist() throws Exception {
        when(fileSystemService.getFramesCount(PROJECT_ID_OUT_OF_RANGE))
                .thenThrow(new NotFoundException(NOT_FOUND_MSG, FileSystemService.class.getSimpleName()));

        mockMvc.perform(get("/api/projects/{id}/frame/count", PROJECT_ID_OUT_OF_RANGE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message").value(NOT_FOUND_MSG));
    }
}
