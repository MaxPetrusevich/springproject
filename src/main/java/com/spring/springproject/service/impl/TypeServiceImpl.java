package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Type;
import com.spring.springproject.repositories.TypeRepository;
import com.spring.springproject.service.impl.specifications.TypeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl {

    private final TypeRepository repository;

    
    public Page<Type> findAll(Pageable pageable, String name) {
        // Use the Specification for filtering by name
        Page<Type> types = repository.findAll(
                TypeSpecification.filterByTypeName(name), pageable);
        return new PageImpl<>(types.getContent(), pageable, types.getTotalElements());
    }

    
    public Set<Type> findAll() {
        return new HashSet<>(repository.findAll());
    }

    
    public Type findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    
    @Transactional
    public Type save(Type type) {
        if (type.getType() == null || type.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be empty");
        }
        return repository.save(type);
    }

    
    @Transactional
    public void update(Type type) {
        repository.save(type); // No need for a custom update method, since save will update existing records
    }

    
    public void delete(Long id) {
        repository.deleteById(id);
    }

    
    @Transactional
    public void update(Long id, String name) {
        Type type = repository.findById(id).orElse(null);
        if (type != null) {
            type.setType(name);
            repository.save(type); // Update the type name
        }
    }

    
    @Transactional
    public Type save(String name) {
        Type type = new Type();
        type.setType(name);
        return repository.save(type);
    }
}
