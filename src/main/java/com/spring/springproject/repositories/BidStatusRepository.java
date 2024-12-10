package com.spring.springproject.repositories;

import com.spring.springproject.entities.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BidStatusRepository extends JpaRepository<BidStatus, Long>, JpaSpecificationExecutor<BidStatus> {
}
