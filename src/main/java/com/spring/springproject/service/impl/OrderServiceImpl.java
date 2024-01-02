package com.spring.springproject.service.impl;

import com.spring.springproject.dto.OrderDto;
import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.Order;
import com.spring.springproject.entities.Status;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.OrderRepository;
import com.spring.springproject.repositories.specifications.OrderSpecifications;
import com.spring.springproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public void saveOrder(OrderDto order) {
        order.setOrderDate(new Date()); // Set the order date to the current date
        orderRepository.saveOrder(mapper.map(order,Order.class));
    }

    public OrderDto getOrderById(Integer id) {
        return orderRepository.findById(id)
                .map(order -> mapper.map(order,OrderDto.class))
                .orElse(null);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository
                .findAll()
                .stream()
                .map(order -> mapper.map(order,OrderDto.class))
                .collect(Collectors.toList());
    }

    public void updateOrder(OrderDto order) {
        orderRepository.update(mapper.map(order, Order.class));
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }

    public List<OrderDto> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findAllByUser(user,pageable).getContent()
                .stream()
                .map(order -> mapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }
    public List<OrderDto> getOrdersByUser(User user) {
        return orderRepository.findAllByUser(user)
                .stream()
                .map(order -> mapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersLastMonth(Pageable pageable) {
        LocalDate localDate = LocalDate.now();
        return orderRepository.findAllInLastMonth(localDate.minusMonths(1),pageable)
                .getContent()
                .stream()
                .map(order -> mapper.map(order,OrderDto.class))
                .collect(Collectors.toList());
    }

    public OrderDto getBucket(Integer userId){
        OrderDto orderDto =  orderRepository.getBucket(userId).stream()
              .map(order -> mapper.map(order,OrderDto.class))
              .findFirst().orElse(null);
        if(orderDto == null){
            UserDto userDto = userRepository.findById(userId).map(user -> mapper.map(user,UserDto.class)).orElse(null);
            orderDto = OrderDto.builder().status(Status.BUCKET).amount(0.0).orderDate(new Date()).user(userDto).build();
            orderRepository.saveOrder(mapper.map(orderDto, Order.class));
        }
        orderDto = mapper.map(orderRepository.getBucket(userId), OrderDto.class);
        return orderDto;
    }

    public Page<OrderDto> findAll(Pageable pageable, UserDto user, Double minAmount, Double maxAmount,
                                  Date startDate, Date endDate, Status status) {
        User userEntity = mapper.map(user, User.class);
        Specification<Order> spec = OrderSpecifications.withFilters(userEntity, minAmount, maxAmount, status, startDate, endDate);
        return orderRepository.findAll(spec, pageable).map(order -> mapper.map(order, OrderDto.class));
    }

}

