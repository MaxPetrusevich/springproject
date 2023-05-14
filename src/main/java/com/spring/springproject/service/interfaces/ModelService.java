package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.dto.TypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;


public interface ModelService extends Service<ModelDto> {
    Page<ModelDto> findAll(Pageable pageable, String name);
    void update(Integer id, String name);
    ModelDto save(String name);


}
