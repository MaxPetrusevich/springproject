package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.TechniqueDto;

import java.util.Set;


public interface TechniqueService extends Service<TechniqueDto>{
    Set<TechniqueDto> findByPriceBetween(Double startPrice, Double endPrice);
}
