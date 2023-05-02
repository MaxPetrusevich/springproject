package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.TechniqueDto;

import java.util.Set;


public interface TechniqueService extends Service<TechniqueDto> {
    Set<TechniqueDto> findByPriceBetween(Double startPrice, Double endPrice);
    void update(Integer producerId, Integer modelId, Integer categoryId,
                Double price, Integer[] storeIdes,  Integer id);
    TechniqueDto save(Integer producerId, Integer modelId, Integer categoryId,
                      Double price, Integer[] storeIdes);
}
