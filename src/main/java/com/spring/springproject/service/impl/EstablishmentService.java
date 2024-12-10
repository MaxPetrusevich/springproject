package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Establishment;
import com.spring.springproject.repositories.EstablishmentRepository;
import com.spring.springproject.service.impl.specifications.EstablishmentSpecification;
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
public class EstablishmentService {

    private final EstablishmentRepository repository;

    // Find all Establishments with pagination and optional name filtering
    public Page<Establishment> findAll(Pageable pageable, String name) {
        Page<Establishment> establishments = repository.findAll(
                EstablishmentSpecification.filterByName(name), pageable);
        return new PageImpl<>(establishments.getContent(), pageable, establishments.getTotalElements());
    }

    // Find all Establishments without pagination
    public Set<Establishment> findAll() {
        return new HashSet<>(repository.findAll());
    }

    // Find an Establishment by its ID
    public Establishment findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // Save a new Establishment
    @Transactional
    public Establishment save(Establishment establishment) {
        return repository.save(establishment);
    }

    // Update an existing Establishment
    @Transactional
    public void update(Establishment establishment) {
        repository.save(establishment); // Save will handle both create and update operations
    }

    // Delete an Establishment by its ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Save a new Establishment with a given name
    @Transactional
    public Establishment save(String name) {
        Establishment establishment = new Establishment();
        establishment.setName(name);
        return repository.save(establishment);
    }
}
