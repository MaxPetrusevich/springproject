package com.example.dto;

import com.example.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String avatarUrl;
    private String password;
    
    private int activeSubscriptionsCount;
    private int totalSubscriptionsCount;
    private double totalSpent;
} 