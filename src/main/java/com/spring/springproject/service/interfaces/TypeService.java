package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.TypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;


public interface TypeService extends Service<TypeDto> {
    void update(Integer id, String name);
    TypeDto save(String name);
    Page<TypeDto> findAll(Pageable pageable, String name);
}
