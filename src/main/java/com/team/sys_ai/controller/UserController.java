package com.team.sys_ai.controller;

import com.team.sys_ai.dto.PageResponse;
import com.team.sys_ai.dto.UserCreateDTO;
import com.team.sys_ai.dto.UserDTO;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * Get all users (paginated).
     */
    @GetMapping
    public ResponseEntity<PageResponse<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(userService.getAllUsers(pageable)));
    }

    /**
     * Get active users (paginated).
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponse<UserDTO>> getActiveUsers(
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(userService.getActiveUsers(pageable)));
    }

    /**
     * Get users by role (paginated).
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<PageResponse<UserDTO>> getUsersByRole(
            @PathVariable Role role,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(userService.getUsersByRole(role, pageable)));
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Get gestionnaires assigned to a warehouse.
     */
    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<UserDTO>> getGestionnairesByEntrepot(@PathVariable Long entrepotId) {
        return ResponseEntity.ok(userService.getGestionnairesByEntrepot(entrepotId));
    }

    /**
     * Create new user.
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    /**
     * Update user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    /**
     * Update user password.
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign user to warehouse.
     */
    @PatchMapping("/{userId}/assign/{entrepotId}")
    public ResponseEntity<UserDTO> assignToEntrepot(
            @PathVariable Long userId,
            @PathVariable Long entrepotId) {
        return ResponseEntity.ok(userService.assignToEntrepot(userId, entrepotId));
    }

    /**
     * Deactivate user.
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    /**
     * Activate user.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    /**
     * Delete user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
