package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.dto.TypeDto;

import java.util.Set;


public interface ModelService extends Service<ModelDto> {
    Set<ModelDto> findByName(String name);
    void update(Integer id, String name);
    ModelDto save(String name);


}
