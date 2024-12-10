package com.spring.springproject.service.impl;

import com.spring.springproject.entities.BidStatus;
import com.spring.springproject.repositories.BidStatusRepository;
import com.spring.springproject.service.impl.specifications.BidStatusSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidStatusService {

    private final BidStatusRepository bidStatusRepository;

    // Find all BidStatuses with pagination and optional filters
    public Page<BidStatus> findAll(Pageable pageable, String status) {
        Page<BidStatus> bidStatuses = bidStatusRepository.findAll(
                BidStatusSpecification.filterBidStatuses(status), pageable);
        return new PageImpl<>(bidStatuses.getContent(), pageable, bidStatuses.getTotalElements());
    }

    // Find all BidStatuses without pagination
    public List<BidStatus> findAll(String status) {
        return bidStatusRepository.findAll(BidStatusSpecification.filterBidStatuses(status));
    } 
    
    public List<BidStatus> findAll() {
        return bidStatusRepository.findAll();
    }

    // Find a BidStatus by ID
    public BidStatus findById(Long id) {
        return bidStatusRepository.findById(id).orElse(null);
    }

    // Save a new BidStatus
    public BidStatus save(String status) {
        BidStatus bidStatus = BidStatus.builder()
                .status(status)
                .build();
        return bidStatusRepository.save(bidStatus);
    }

    // Update an existing BidStatus
    public void update(Long id, String status) {
        BidStatus bidStatus = bidStatusRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("BidStatus not found"));
        bidStatus.setStatus(status);
        bidStatusRepository.save(bidStatus);
    }

    // Delete a BidStatus by ID
    public void delete(Long id) {
        bidStatusRepository.deleteById(id);
    }
}
