package com.spring.springproject.service.impl;


import com.spring.springproject.dto.OrderTechniqueDto;
import com.spring.springproject.entities.OrderTechnique;
import com.spring.springproject.repositories.OrderTechniqueRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderTechniqueServiceImpl {

    private final OrderTechniqueRepository orderTechniqueRepository;
    private final ModelMapper mapper;

    public void saveOrderTechnique(OrderTechniqueDto orderTechniqueDto) {
        orderTechniqueRepository.saveOrderTechnique(mapper.map(orderTechniqueDto, OrderTechnique.class));
    }

    public OrderTechniqueDto getOrderTechniqueById(Integer id) {
        return orderTechniqueRepository.findById(id)
                .map(orderTechnique -> mapper.map(orderTechnique, OrderTechniqueDto.class))
                .orElse(null);
    }

    public List<OrderTechniqueDto> getAllOrderTechniques() {
        return orderTechniqueRepository
                .findAll()
                .stream()
                .map(orderTechnique -> mapper.map(orderTechnique, OrderTechniqueDto.class))
                .collect(Collectors.toList());
    }

    public void updateOrderTechnique(OrderTechniqueDto orderTechniqueDto) {
        orderTechniqueRepository.update(mapper.map(orderTechniqueDto, OrderTechnique.class));
    }

    public void deleteOrderTechniqueById(Integer id) {
        orderTechniqueRepository.deleteById(id);
    }

    public Page<OrderTechniqueDto> searchOrderTechniques(OrderTechniqueDto orderTechniqueDto, Integer minQuantity, Integer maxQuantity, Double minOrderAmount, Double maxOrderAmount, Pageable pageable) {
        return orderTechniqueRepository.findAll(
                mapper.map(orderTechniqueDto, OrderTechnique.class),
                minQuantity,
                maxQuantity,
                minOrderAmount,
                maxOrderAmount,
                pageable
        ).map(orderTechnique -> mapper.map(orderTechnique, OrderTechniqueDto.class));
    }

    public OrderTechniqueDto findByTechniqueIdAndOrderId(Integer techId, Integer orderId){
        return orderTechniqueRepository.findByTechniqueIdAndOrderId(techId, orderId)
              .map(orderTechnique -> mapper.map(orderTechnique, OrderTechniqueDto.class))
              .orElse(null);
    }
}
