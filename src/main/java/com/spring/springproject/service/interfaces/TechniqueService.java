package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.TechniqueDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TechniqueService extends Service<TechniqueDto> {
    void update(Integer producerId, Integer modelId, Integer categoryId,
                Double price, Integer[] storeIdes, Integer id);

    TechniqueDto save(Integer producerId, Integer modelId, Integer categoryId,
                      Double price, Integer[] storeIdes);

    Page<TechniqueDto> findAll(Pageable pageable, Double startPrice, Double endPrice);
}
