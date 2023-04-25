package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.ModelDto;

import java.util.Set;


public interface ModelService extends Service<ModelDto>{
    Set<ModelDto> findByName(String name);


}
