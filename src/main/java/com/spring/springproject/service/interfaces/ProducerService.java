package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.dto.TypeDto;

import java.util.Set;


public interface ProducerService extends Service<ProducerDto> {
    Set<ProducerDto> findByName(String name);

    Set<ProducerDto> findByCountry(String country);
    void update(Integer id, String name, String country);
    ProducerDto save(String name, String country);

}
