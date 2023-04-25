package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.TypeDto;

import java.util.Set;


public interface TypeService extends Service<TypeDto>{
    Set<TypeDto> findByName(String name);
}
