package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.ProducerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProducerService extends Service<ProducerDto> {
    Page<ProducerDto> findAll(Pageable pageable, String name, String country);

    void update(Integer id, String name, String country);

    ProducerDto save(String name, String country);

}
