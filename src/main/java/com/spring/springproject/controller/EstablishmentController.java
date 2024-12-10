package com.spring.springproject.controller;

import com.spring.springproject.entities.Establishment;
import com.spring.springproject.service.impl.EstablishmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/establishments")
@RequiredArgsConstructor
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    // Get all establishments with pagination and optional name filter
    @GetMapping
    public ResponseEntity<Page<Establishment>> getAllEstablishments(Pageable pageable,
                                                                    @RequestParam(required = false) String name) {
        Page<Establishment> establishments = establishmentService.findAll(pageable, name);
        return new ResponseEntity<>(establishments, HttpStatus.OK);
    }

    // Get an establishment by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Establishment> getEstablishmentById(@PathVariable Long id) {
        Establishment establishment = establishmentService.findById(id);
        if (establishment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(establishment, HttpStatus.OK);
    }

    // Save a new establishment
    @PostMapping
    public ResponseEntity<Establishment> createEstablishment(@RequestBody Establishment establishment) {
        Establishment savedEstablishment = establishmentService.save(establishment);
        return new ResponseEntity<>(savedEstablishment, HttpStatus.CREATED);
    }

    // Update an existing establishment
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEstablishment(@PathVariable Long id, @RequestBody Establishment establishment) {
        Establishment existingEstablishment = establishmentService.findById(id);
        if (existingEstablishment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        establishment.setId(id);
        establishmentService.update(establishment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Delete an establishment by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstablishment(@PathVariable Long id) {
        Establishment establishment = establishmentService.findById(id);
        if (establishment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        establishmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Save a new establishment with a given name
    @PostMapping("/saveWithName")
    public ResponseEntity<Establishment> saveEstablishmentWithName(@RequestParam String name) {
        Establishment establishment = establishmentService.save(name);
        return new ResponseEntity<>(establishment, HttpStatus.CREATED);
    }
}
