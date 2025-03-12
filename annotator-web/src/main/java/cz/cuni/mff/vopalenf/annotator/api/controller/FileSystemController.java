package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.FrameCount;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponse;
import cz.cuni.mff.vopalenf.annotator.service.FileSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "File System", description = "Endpoints for handling project frames and log files")
@RestController
@RequestMapping("/api")
public class FileSystemController {

    private final FileSystemService fileSystemService;

    @Autowired
    public FileSystemController(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }


    @Operation(
            summary = "Get a specific frame from a project",
            description = "Retrieves a single frame from the specified project at the given position.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "projectId", schema = @Schema(type = "integer"), description = "ID of the project"),
                    @Parameter(in = ParameterIn.PATH, name = "position", schema = @Schema(type = "integer"), description = "Position of the frame within the project")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Frame retrieved successfully", content = @Content(mediaType = "image/jpeg")),
                    @ApiResponse(responseCode = "404", description = "Frame or project not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Image fetching failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(
            value = "/projects/{projectId}/frame/{position}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<Resource> getFrame(@PathVariable Long projectId, @PathVariable Integer position) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(fileSystemService.getFrame(projectId, position));
    }

    @Operation(
            summary = "Get the total number of frames in a project",
            description = "Retrieves the count of available frames in the specified project.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), description = "ID of the project")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Frame count retrieved successfully", content = @Content(schema = @Schema(implementation = FrameCount.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(
            value = "/projects/{id}/frame/count",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FrameCount> getFramesCount(@PathVariable Long id) {
        return ResponseEntity.ok(fileSystemService.getFramesCount(id));
    }
}
