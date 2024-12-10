package com.spring.springproject.repositories;

import com.spring.springproject.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    // Dynamic query execution with conditions
    @Query("SELECT p FROM Payment p WHERE p.date = :date")
    List<Payment> findByDate(@Param("date") LocalDate date);
}
