package com.spring.springproject.repositories;

import com.spring.springproject.entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long>, JpaSpecificationExecutor<Bid> {
    // Базовые методы уже предоставлены JpaRepository и JpaSpecificationExecutor
}
