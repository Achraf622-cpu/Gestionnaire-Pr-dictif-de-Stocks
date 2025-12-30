package com.team.sys_ai.controller;

import com.team.sys_ai.dto.UserCreateDTO;
import com.team.sys_ai.dto.UserDTO;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<UserDTO>> getGestionnairesByEntrepot(@PathVariable Long entrepotId) {
        return ResponseEntity.ok(userService.getGestionnairesByEntrepot(entrepotId));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/assign/{entrepotId}")
    public ResponseEntity<UserDTO> assignToEntrepot(
            @PathVariable Long userId,
            @PathVariable Long entrepotId) {
        return ResponseEntity.ok(userService.assignToEntrepot(userId, entrepotId));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
