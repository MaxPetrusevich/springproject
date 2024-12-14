package com.example.service;

import com.example.dto.OrganisationDto;
import com.example.entity.Organisation;
import com.example.repository.OrganisationRepository;
import com.example.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {
    
    private final OrganisationRepository organisationRepository;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    
    @Transactional(readOnly = true)
    public List<Organisation> findAll() {
        return organisationRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Organisation findById(Long id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
    }
    
    @Transactional
    public Organisation create(OrganisationDto dto) {
        Organisation organisation = new Organisation();
        organisation.setName(dto.getName());
        organisation.setDescription(dto.getDescription());
        organisation.setOwner(userService.findById(dto.getOwnerId()));
        organisation.setActive(true);
        return organisationRepository.save(organisation);
    }
    
    @Transactional
    public Organisation update(Long id, OrganisationDto dto) {
        Organisation organisation = findById(id);
        organisation.setName(dto.getName());
        organisation.setDescription(dto.getDescription());
        return organisationRepository.save(organisation);
    }
    
    @Transactional
    public void delete(Long id) {
        organisationRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Organisation> findRecent(int limit) {
        return organisationRepository.findTop5ByOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public Page<Organisation> findAll(String search, Boolean active, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return organisationRepository.findByNameContainingOrDescriptionContaining(
                search, search, pageable);
        }
        if (active != null) {
            return organisationRepository.findByActive(active, pageable);
        }
        return organisationRepository.findAll(pageable);
    }
    
    @Transactional
    public void toggleStatus(Long id) {
        Organisation organisation = findById(id);
        organisation.setActive(!organisation.getActive());
        organisationRepository.save(organisation);
    }
    
    @Transactional(readOnly = true)
    public List<Organisation> findByOwnerId(Long userId) {
        return organisationRepository.findByOwnerId(userId);
    }
    
    public long countSubscribers(Long organisationId) {
        return subscriptionRepository.countByOrganisationId(organisationId);
    }
    
    public boolean isOwner(Long organisationId, Long userId) {
        return organisationRepository.existsByIdAndOwnerId(organisationId, userId);
    }
} 