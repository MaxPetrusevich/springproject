package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Role;
import com.spring.springproject.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;

    // Добавим метод findByName для регистрации
    public Role findByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
            .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
    }

    @Transactional(readOnly = true)
    public Page<Role> findAll(Pageable pageable, String roleName) {
        if (roleName != null && !roleName.isEmpty()) {
            return roleRepository.findByRoleNameContainingIgnoreCase(roleName, pageable);
        }
        return roleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findById(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Transactional
    public Role create(Role role) {
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role update(Long id, Role role) {
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        Role existingRole = findById(id);
        existingRole.setRoleName(role.getRoleName());
        return roleRepository.save(existingRole);
    }

    @Transactional
    public void delete(Long id) {
        Role role = findById(id);
        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete role with assigned users");
        }
        roleRepository.deleteById(id);
    }
}