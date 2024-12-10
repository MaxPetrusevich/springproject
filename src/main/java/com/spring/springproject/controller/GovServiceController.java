package com.spring.springproject.controller;

import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Establishment;
import com.spring.springproject.entities.GovService;
import com.spring.springproject.service.impl.GovServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/govservices")
@RequiredArgsConstructor
public class GovServiceController {

    private final GovServiceService govServiceService;

    @GetMapping
    public ResponseEntity<Page<GovService>> getAllGovServices(Pageable pageable,
                                                              @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) Long categoryId,
                                                              @RequestParam(required = false) Long establishmentId) {
        Page<GovService> govServices = govServiceService.findAll(pageable, categoryId, establishmentId, name);
        return new ResponseEntity<>(govServices, HttpStatus.OK);
    }

    // Get a GovService by its ID
    @GetMapping("/{id}")
    public ResponseEntity<GovService> getGovServiceById(@PathVariable Long id) {
        GovService govService = govServiceService.findById(id);
        if (govService == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(govService, HttpStatus.OK);
    }

    // Save a new GovService
    @PostMapping
    public ResponseEntity<GovService> createGovService(@RequestBody GovService govService) {
        GovService savedGovService = govServiceService.save(govService);
        return new ResponseEntity<>(savedGovService, HttpStatus.CREATED);
    }

    // Update an existing GovService
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGovService(@PathVariable Long id, @RequestBody GovService govService) {
        GovService existingGovService = govServiceService.findById(id);
        if (existingGovService == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        govService.setId(id);
        govServiceService.update(govService);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Delete a GovService by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGovService(@PathVariable Long id) {
        GovService govService = govServiceService.findById(id);
        if (govService == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        govServiceService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Save a GovService with a given name and description
    @PostMapping("/saveWithDetails")
    public ResponseEntity<GovService> saveGovServiceWithDetails(@RequestParam String name,
                                                                @RequestParam String description,
                                                                @RequestParam Category category,
                                                                @RequestParam Establishment establishment) {
        GovService govService = GovService.builder()
                .name(name)
                .description(description)
                .category(category)
                .establishment(establishment)
                .build();
        return new ResponseEntity<>(govServiceService.save(govService), HttpStatus.CREATED);
    }
}
