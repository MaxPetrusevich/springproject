package com.spring.springproject.service;

import com.spring.springproject.entities.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
public interface BidService {
    Bid save(Bid bid);
    Bid findById(Long id);
    void delete(Long id);
    void update(Long id, Bid bid);
    Page<Bid> findAll(Pageable pageable, Long citizenId, Long serviceId, Long statusId);
    List<Bid> findRecent(int limit);
    long count();
    List<Bid> findAll();
    long countActive();
    // Добавляем новый метод
    List<Bid> findAllByCitizen(Long citizenId);
    Page<Bid> findAllAdmin(Pageable pageable, Long citizenId, Long serviceId, Long statusId, Long establishmentId);
} 