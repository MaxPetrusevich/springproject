package com.spring.springproject.service.impl;

import com.spring.springproject.entities.GovService;
import com.spring.springproject.repositories.GovServiceRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GovServiceService {

    private final GovServiceRepository repository;

    public long count() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public Page<GovService> findAll(Pageable pageable, Long categoryId, Long establishmentId, String name) {
        if (name != null && !name.isEmpty()) {
            if (categoryId != null && establishmentId != null) {
                Page<GovService> page = repository.findByNameContainingIgnoreCaseAndCategoryIdAndEstablishmentId(
                        name, categoryId, establishmentId, pageable);
                initializeEntities(page);
                return page;
            }
            if (categoryId != null) {
                Page<GovService> page = repository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable);
                initializeEntities(page);
                return page;
            }
            if (establishmentId != null) {
                Page<GovService> page = repository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId, pageable);
                initializeEntities(page);
                return page;
            }
            Page<GovService> page = repository.findByNameContainingIgnoreCase(name, pageable);
            initializeEntities(page);
            return page;
        }

        return findAll(pageable, categoryId, establishmentId); // Используем существующую реализацию
    }


    @Transactional(readOnly = true)
    public Page<GovService> findAll(Pageable pageable, Long categoryId, Long establishmentId) {
        if (categoryId != null && establishmentId != null) {
            Page<GovService> page = repository.findByCategoryIdAndEstablishmentId(categoryId, establishmentId, pageable);
            initializeEntities(page);
            return page;
        }
        if (categoryId != null) {
            Page<GovService> page = repository.findByCategoryId(categoryId, pageable);
            initializeEntities(page);
            return page;
        }
        if (establishmentId != null) {
            Page<GovService> page = repository.findByEstablishmentId(establishmentId, pageable);
            initializeEntities(page);
            return page;
        }

        Page<GovService> page = repository.findAllPaged(pageable);
        initializeEntities(page);
        return page;
    }

    public List<GovService> findAll() {
        return repository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public GovService findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public GovService save(GovService govService) {
        return repository.save(govService);
    }

    @Transactional
    public void update(GovService govService) {
        repository.save(govService);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void initializeEntities(Page<GovService> page) {
        page.getContent().forEach(service -> {
            Hibernate.initialize(service.getCategory());
            Hibernate.initialize(service.getEstablishment());
        });
    }
}
