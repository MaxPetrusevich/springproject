package com.spring.springproject.controller;

import com.spring.springproject.dto.BidRequestDto;
import com.spring.springproject.entities.Bid;
import com.spring.springproject.entities.BidStatus;
import com.spring.springproject.entities.Citizen;
import com.spring.springproject.entities.GovService;
import com.spring.springproject.service.BidService;
import com.spring.springproject.service.impl.CitizenService;
import com.spring.springproject.service.impl.GovServiceService;
import com.spring.springproject.service.impl.BidStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final CitizenService citizenService;
    private final GovServiceService govServiceService;
    private final BidStatusService bidStatusService;

    // Get all bids with optional filtering
    @GetMapping
    public ResponseEntity<Page<Bid>> getBids(
            @RequestParam(value = "citizenId", required = false) Long citizenId,
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @RequestParam(value = "statusId", required = false) Long statusId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Bid> bids = bidService.findAll(pageable, citizenId, serviceId, statusId);

        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    // Get a bid by ID
    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long id) {
        Bid bid = bidService.findById(id);
        if (bid != null) {
            return new ResponseEntity<>(bid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Create a new bid
    @PostMapping
    public ResponseEntity<Bid> createBid(@RequestBody @Valid BidRequestDto bidRequestDto) {
        // Find the related entities from the service layer
        Citizen citizen = citizenService.findById(bidRequestDto.getCitizenId());
        GovService govService = govServiceService.findById(bidRequestDto.getServiceId());
        BidStatus bidStatus = bidStatusService.findById(bidRequestDto.getStatusId());

        // If any of the related entities are not found, return a bad request
        if (citizen == null || govService == null || bidStatus == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Create the Bid entity
        Bid bid = new Bid();
        bid.setCitizen(citizen);
        bid.setService(govService);
        bid.setStatus(bidStatus);
        bid.setDate(bidRequestDto.getDate() != null ? bidRequestDto.getDate() : LocalDate.now());

        // Save and return the created bid
        Bid createdBid = bidService.save(bid);
        return new ResponseEntity<>(createdBid, HttpStatus.CREATED);
    }

    // Update an existing bid
    @PutMapping("/{id}")
    public ResponseEntity<Bid> updateBid(@PathVariable Long id, @RequestBody @Valid BidRequestDto bidRequestDto) {
        // Find the related entities from the service layer
        Citizen citizen = citizenService.findById(bidRequestDto.getCitizenId());
        GovService govService = govServiceService.findById(bidRequestDto.getServiceId());
        BidStatus bidStatus = bidStatusService.findById(bidRequestDto.getStatusId());

        // If any of the related entities are not found, return a bad request
        if (citizen == null || govService == null || bidStatus == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Find the existing bid
        Bid existingBid = bidService.findById(id);
        if (existingBid == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Update the Bid entity
        existingBid.setCitizen(citizen);
        existingBid.setService(govService);
        existingBid.setStatus(bidStatus);
        existingBid.setDate(bidRequestDto.getDate() != null ? bidRequestDto.getDate() : LocalDate.now());

        // Save the updated bid
        Bid updatedBid = bidService.save(existingBid);
        return new ResponseEntity<>(updatedBid, HttpStatus.OK);
    }

    // Delete a bid by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long id) {
        Bid bid = bidService.findById(id);
        if (bid != null) {
            bidService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
