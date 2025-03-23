package cz.cuni.mff.vopalenf.annotator.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.request.TeamRequest;
import cz.cuni.mff.vopalenf.annotator.api.request.UserRequest;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import cz.cuni.mff.vopalenf.annotator.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Teams", description = "Endpoints for managing teams")
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(
            summary = "Get all teams",
            description = "Retrieves a list of all teams along with their users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Teams retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can access this endpoint")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @JsonView({Views.ShowUsersInTeams.class})
    @GetMapping
    public ResponseEntity<List<Team>> getTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Operation(
            summary = "Delete a team",
            description = "Deletes a team by its ID.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "teamId", schema = @Schema(type = "integer"), description = "ID of the team to be deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Team deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can delete teams"),
                    @ApiResponse(responseCode = "404", description = "Team not found")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Create a new team",
            description = "Creates a new team with the given details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Team request object containing necessary team details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Team created successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can create users")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Team> createTeam(@RequestBody TeamRequest teamRequest) {
        Team team = teamService.createTeam(teamRequest);
        return ResponseEntity.ok(team);
    }

    @Operation(
            summary = "Update a team",
            description = "Updates a team information by their ID.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "teamId", schema = @Schema(type = "integer"), description = "ID of the team to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Team request object containing updated team details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can update users"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Team> updateTeam(@PathVariable Long teamId, @RequestBody TeamRequest team) {
        Team teamToUpdate = teamService.updateTeam(teamId, team);
        return ResponseEntity.ok(teamToUpdate);
    }


    @Operation(
            summary = "Add a member to a team",
            description = "Adds a new member to the specified team.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "teamId", schema = @Schema(type = "integer"), description = "ID of the team to add the member to", required = true),
                    @Parameter(in = ParameterIn.PATH, name = "userId", schema = @Schema(type = "integer"), description = "ID of the user to add to the team", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User request object containing necessary user details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member added successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can add members to teams"),
                    @ApiResponse(responseCode = "404", description = "Team or user not found")
            }
    )
    @PostMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> addTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.addTeamMember(teamId, userId);
        return ResponseEntity.ok().build();
    }
}
