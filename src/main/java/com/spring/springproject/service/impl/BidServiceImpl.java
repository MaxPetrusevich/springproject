package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Bid;
import com.spring.springproject.repositories.BidRepository;
import com.spring.springproject.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;

    @Override
    @Transactional
    public Bid save(Bid bid) {
        return bidRepository.save(bid);
    }

    @Override
    @Transactional(readOnly = true)
    public Bid findById(Long id) {
        return bidRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена с ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bidRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(Long id, Bid bid) {
        if (!bidRepository.existsById(id)) {
            throw new EntityNotFoundException("Заявка не найдена с ID: " + id);
        }
        bid.setId(id);
        bidRepository.save(bid);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bid> findAllAdmin(Pageable pageable, Long citizenId, Long serviceId, Long statusId, Long establishmentId) {
        Specification<Bid> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (citizenId != null) {
                predicates.add(cb.equal(root.get("citizen").get("id"), citizenId));
            }
            if (serviceId != null) {
                predicates.add(cb.equal(root.get("service").get("id"), serviceId));
            }
            if (statusId != null) {
                predicates.add(cb.equal(root.get("status").get("id"), statusId));
            }
            if (establishmentId != null) {
                predicates.add(cb.equal(root.get("service").get("establishment").get("id"), establishmentId));
            }

            // Сортировка по дате создания (от новых к старым)
            query.orderBy(cb.desc(root.get("date")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return bidRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bid> findAll(Pageable pageable, Long citizenId, Long serviceId, Long statusId) {
        Specification<Bid> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (citizenId != null) {
                predicates.add(cb.equal(root.get("citizen").get("id"), citizenId));
            }
            if (serviceId != null) {
                predicates.add(cb.equal(root.get("service").get("id"), serviceId));
            }
            if (statusId != null) {
                predicates.add(cb.equal(root.get("status").get("id"), statusId));
            }
            
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return bidRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bid> findRecent(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "date"));
        return bidRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return bidRepository.count();
    }

    @Override
    public List<Bid> findAll() {
        return bidRepository.findAll();
    }

    @Override
    public long countActive() {
        return bidRepository.findAll().stream().filter(bid -> bid.getStatus().getStatus().equals("НОВАЯ") || bid.getStatus().getStatus().equals("В ОБРАБОТКЕ")).count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bid> findAllByCitizen(Long citizenId) {
        Specification<Bid> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (citizenId != null) {
                predicates.add(cb.equal(root.get("citizen").get("id"), citizenId));
            }

            // Сортировка по дате создания (от новых к старым)
            query.orderBy(cb.desc(root.get("date")));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return bidRepository.findAll(spec);
    }
} 