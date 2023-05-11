package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.dto.TypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;


public interface StoreService extends Service<StoreDto> {

    void update(Integer id, String name, String address);
    StoreDto save(String name, String address);
    Page<StoreDto> findAll(Pageable pageable, String name, String address);

}
