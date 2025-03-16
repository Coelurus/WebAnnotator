package cz.cuni.mff.vopalenf.aimock.api.controller;

import cz.cuni.mff.vopalenf.aimock.api.model.LogData;
import cz.cuni.mff.vopalenf.aimock.api.model.PredictionTriple;
import cz.cuni.mff.vopalenf.aimock.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @Operation(
            summary = "Train AI model on a project",
            description = "Trains an AI model based on the annotations in a project.",
            parameters = {
                    @Parameter(name = "projectId", description = "Project ID", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "AI model trained successfully"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
            }
    )
    @PostMapping("/{projectId}")
    public ResponseEntity<List<PredictionTriple>> trainAI(
            @PathVariable Long projectId,
            @RequestBody List<LogData> logData
    ) {
        aiService.trainAI(projectId, logData);
        return ResponseEntity.ok(aiService.test(logData));
    }
}
