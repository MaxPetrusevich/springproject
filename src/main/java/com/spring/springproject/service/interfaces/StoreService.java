package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.dto.TypeDto;

import java.util.Set;


public interface StoreService extends Service<StoreDto> {
    Set<StoreDto> findByName(String name);

    Set<StoreDto> findByAddress(String address);
    void update(Integer id, String name, String address);
    StoreDto save(String name, String address);
}
