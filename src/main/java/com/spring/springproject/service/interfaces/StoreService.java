package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.StoreDto;

import java.util.Set;


public interface StoreService extends Service<StoreDto> {
    Set<StoreDto> findByName(String name);

    Set<StoreDto> findByAddress(String address);

}
