package com.example.controller;

import com.example.dto.UserDto;
import com.example.enums.UserRole;
import com.example.mapper.EntityMapper;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final EntityMapper mapper;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(mapper.toUserDtos(userService.findAll()));
    }
    
    @PutMapping("/users/{id}/block")
    public ResponseEntity<UserDto> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(userService.blockUser(id)));
    }
    
    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<UserDto> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(userService.unblockUser(id)));
    }
    
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDto> changeUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role
    ) {
        return ResponseEntity.ok(mapper.toDto(userService.changeRole(id, role)));
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
} 