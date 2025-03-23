package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.request.UserRequest;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

@Tag(name = "Users", description = "Endpoints for managing users and roles")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can access this endpoint")
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the given details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User request object containing necessary user details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", description = "Username already exists"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can create users")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes user by their ID.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "userId", schema = @Schema(type = "integer"), description = "ID of the user to be deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can delete users"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update a user",
            description = "Updates a user's information by their ID.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "userId", schema = @Schema(type = "integer"), description = "ID of the user to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User request object containing updated user details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can update users"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UserRequest user) {
        User userToUpdate = userService.updateUser(userId, user);
        return ResponseEntity.ok(userToUpdate);
    }

    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all available roles in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Only admins can access this endpoint")
            }
    )
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = userService.getAllRoles();
        return ResponseEntity.ok().body(roles);
    }
}
