package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.dto.ProducerDto;

import java.util.Set;


public interface ProducerService extends Service<ProducerDto> {
    Set<ProducerDto> findByName(String name);

    Set<ProducerDto> findByCountry(String country);

}
