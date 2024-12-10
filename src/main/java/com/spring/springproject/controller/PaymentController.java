package com.spring.springproject.controller;

import com.spring.springproject.entities.Payment;
import com.spring.springproject.service.impl.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        return ResponseEntity.ok(payments);
    }

    // Get payments by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Payment>> getPaymentsByDate(@PathVariable("date") LocalDate date) {
        List<Payment> payments = paymentService.findByDate(date);
        return ResponseEntity.ok(payments);
    }

    // Get a Payment by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") Long id) {
        Optional<Payment> payment = paymentService.findById(id);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new Payment
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment)  {
        Payment createdPayment = paymentService.save(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    // Update an existing Payment
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable("id") Long id, @RequestBody Payment payment) {
        Payment updatedPayment = paymentService.update(id, payment);
        return updatedPayment != null ? ResponseEntity.ok(updatedPayment) : ResponseEntity.notFound().build();
    }

    // Delete a Payment by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
