package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Order;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl {

    private final OrderRepository orderRepository;


    public void saveOrder(Order order) {
        order.setOrderDate(new Date()); // Set the order date to the current date
        orderRepository.saveOrder(order);
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrder(Order order) {
        orderRepository.update(order);
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByUser(String userName, Pageable pageable) {
        return orderRepository.findAllByUser(User.builder().userName(userName).build(),pageable).getContent();
    }

    public List<Order> getOrdersLastMonth(Pageable pageable) {
        LocalDate localDate = LocalDate.now();
        return orderRepository.findAllInLastMonth(localDate.minusMonths(1),pageable).getContent();
    }
}

