package com.example.service;

import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.enums.UserRole;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        return userRepository.save(user);
    }
    
    @Transactional
    public User update(Long id, User user) {
        User existingUser = findById(id);
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }
    
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    

    
    @Transactional
    public User blockUser(Long id) {
        User user = findById(id);
        user.setEnabled(false);
        return userRepository.save(user);
    }
    
    @Transactional
    public User unblockUser(Long id) {
        User user = findById(id);
        user.setEnabled(true);
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateProfile(Long id, UserDto userDto) {
        User user = findById(id);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateAvatar(Long id, String avatarUrl) {
        User user = findById(id);
        user.setAvatarUrl(avatarUrl);
       return userRepository.save(user);
    }
    
    @Transactional
    public User changeRole(Long id, UserRole role) {
        User user = findById(id);
        user.setRole(role);
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public List<User> findRecent(int limit) {
        return userRepository.findTop5ByOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public Page<User> findAll(String search, UserRole role, Boolean enabled, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (role != null && enabled != null) {
                return userRepository.findByEmailContainingOrFirstNameContainingOrLastNameContainingAndRoleAndEnabled(
                    search, search, search, role, enabled, pageable);
            } else if (role != null) {
                return userRepository.findByEmailContainingOrFirstNameContainingOrLastNameContainingAndRole(
                    search, search, search, role, pageable);
            } else if (enabled != null) {
                return userRepository.findByEmailContainingOrFirstNameContainingOrLastNameContainingAndEnabled(
                    search, search, search, enabled, pageable);
            } else {
                return userRepository.findByEmailContainingOrFirstNameContainingOrLastNameContaining(
                    search, search, search, pageable);
            }
        } else {
            if (role != null && enabled != null) {
                return userRepository.findByRoleAndEnabled(role, enabled, pageable);
            } else if (role != null) {
                return userRepository.findByRole(role, pageable);
            } else if (enabled != null) {
                return userRepository.findByEnabled(enabled, pageable);
            } else {
                return userRepository.findAll(pageable);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findTop5ByOrderByCreatedAtDesc() {
        return userRepository.findTop5ByOrderByCreatedAtDesc();
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User create(String email, String firstName, String lastName, String password, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updatePassword(Long userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
} 