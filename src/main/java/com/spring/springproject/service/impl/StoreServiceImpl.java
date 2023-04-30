package com.spring.springproject.service.impl;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.entities.Store;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.StoreRepository;
import com.spring.springproject.service.interfaces.StoreService;

import java.util.HashSet;
import java.util.Set;
@Service
@Transactional
public class StoreServiceImpl implements StoreService {

    private final ModelMapper modelMapper;
    private final StoreRepository repository;
    @Autowired
    public StoreServiceImpl(ModelMapper modelMapper, StoreRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

    @Override
    public Set<StoreDto> findAll() {
        Set<StoreDto> storeDtoSet = new HashSet<>();
        for (Store store:
                repository.findAll()) {
            storeDtoSet.add(modelMapper.map(store, StoreDto.class));
        }
        return storeDtoSet;
    }

    @Override
    public StoreDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), StoreDto.class);
    }

    @Override
    public StoreDto save(StoreDto object) {
        Store store = modelMapper.map(object, Store.class);
        store = repository.save(store);
        return modelMapper.map(store, StoreDto.class);
    }

    @Override
    public void update(StoreDto object) {
        repository.save(modelMapper.map(object, Store.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public Set<StoreDto> findByName(String name) {
        Set<StoreDto> storeDtoSet = new HashSet<>();
        for (Store store:
                repository.findByNameContaining(name)) {
            storeDtoSet.add(modelMapper.map(store, StoreDto.class));
        }
        return storeDtoSet;
    }

    @Override
    public Set<StoreDto> findByAddress(String address) {
        Set<StoreDto> storeDtoSet = new HashSet<>();
        for (Store store:
                repository.findByAddressContaining(address)) {
            storeDtoSet.add(modelMapper.map(store, StoreDto.class));
        }
        return storeDtoSet;
    }
}
