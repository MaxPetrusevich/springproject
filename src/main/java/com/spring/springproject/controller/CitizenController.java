package com.spring.springproject.controller;

import com.spring.springproject.entities.Citizen;
import com.spring.springproject.service.impl.CitizenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/citizens")
@RequiredArgsConstructor
public class CitizenController {

    private final CitizenService citizenService;

    // Get all citizens with optional filtering and pagination
    @GetMapping
    public ResponseEntity<Page<Citizen>> getCitizens(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "passportNumber", required = false) String passportNumber,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "identifyNumber", required = false) String identifyNumber,
            Pageable pageable) {

        Page<Citizen> citizens = citizenService.findAll(pageable, firstName, lastName, passportNumber, phone, identifyNumber);
        return new ResponseEntity<>(citizens, HttpStatus.OK);
    }

    // Get a citizen by ID
    @GetMapping("/{id}")
    public ResponseEntity<Citizen> getCitizenById(@PathVariable Long id) {
        Citizen citizen = citizenService.findById(id);
        if (citizen != null) {
            return new ResponseEntity<>(citizen, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get a citizen by Identify Number
    @GetMapping("/identifyNumber/{identifyNumber}")
    public ResponseEntity<Citizen> getCitizenByIdentifyNumber(@PathVariable String identifyNumber) {
        Citizen citizen = citizenService.findByIdentifyNumber(identifyNumber);
        if (citizen != null) {
            return new ResponseEntity<>(citizen, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Create a new citizen
    @PostMapping
    public ResponseEntity<Citizen> createCitizen(@RequestBody @Valid Citizen citizen) {
        Citizen createdCitizen = citizenService.save(citizen);
        return new ResponseEntity<>(createdCitizen, HttpStatus.CREATED);
    }

    // Update an existing citizen
    @PutMapping("/{id}")
    public ResponseEntity<Citizen> updateCitizen(@PathVariable Long id, @RequestBody @Valid Citizen citizen) {
        Citizen existingCitizen = citizenService.findById(id);
        if (existingCitizen != null) {
            citizen.setId(id);
            citizenService.update(citizen);
            return new ResponseEntity<>(citizen, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a citizen by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCitizen(@PathVariable Long id) {
        Citizen existingCitizen = citizenService.findById(id);
        if (existingCitizen != null) {
            citizenService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
