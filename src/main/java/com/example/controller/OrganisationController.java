package com.example.controller;

import com.example.dto.OrganisationDto;
import com.example.entity.User;
import com.example.mapper.EntityMapper;
import com.example.service.OrganisationService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationController extends BaseController {
    
    private final OrganisationService organisationService;
    private final UserService userService;
    private final EntityMapper mapper;
    
    @GetMapping
    public ResponseEntity<List<OrganisationDto>> getAllOrganisations() {
        return ResponseEntity.ok(mapper.toOrganisationDtos(organisationService.findAll()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrganisationDto> getOrganisation(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toOrganisationDto(organisationService.findById(id)));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<OrganisationDto> createOrganisation(@RequestBody OrganisationDto organisationDto) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email);
        Long userId = user.getId();
        organisationDto.setOwnerId(userId);
        return ResponseEntity.ok(mapper.toOrganisationDto(
            organisationService.create(organisationDto)
        ));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<OrganisationDto> updateOrganisation(
            @PathVariable Long id,
            @RequestBody OrganisationDto organisationDto
    ) {
        return ResponseEntity.ok(mapper.toOrganisationDto(
            organisationService.update(id, organisationDto)
        ));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.delete(id);
        return ResponseEntity.ok().build();
    }
} 