package com.spring.springproject.service.impl;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.entities.Store;
import com.spring.springproject.entities.Type;
import com.spring.springproject.specifications.StoreSpecification;
import com.spring.springproject.specifications.TypeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.StoreRepository;
import com.spring.springproject.service.interfaces.StoreService;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        return repository.findAll()
                .stream()
                .map(store -> modelMapper.map(store, StoreDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public StoreDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(store -> modelMapper.map(store, StoreDto.class))
                .findFirst()
                .orElse(null);
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
    public void update(Integer id, String name, String address) {
        Store store = repository.findById(id).orElse(null);
        if (store != null) {
            store.setName(name);
            store.setAddress(address);
            repository.save(store);
        }
    }

    @Override
    public StoreDto save(String name, String address) {
        Store store = new Store();
        store.setName(name);
        store.setAddress(address);
        store = repository.save(store);
        return modelMapper.map(store, StoreDto.class);
    }

    @Override
    public Page<StoreDto> findAll(Pageable pageable, String name, String address) {
        if (!StringUtils.isEmptyOrWhitespace(name) || !StringUtils.isEmptyOrWhitespace(address)) {
            Page<Store> stores = repository.findAll(StoreSpecification.searchStore(name, address), pageable);
            List<StoreDto> storeDtoList = stores
                    .stream()
                    .map(store -> modelMapper.map(store, StoreDto.class))
                    .toList();
            return new PageImpl<>(storeDtoList, pageable, stores.getTotalElements());
        } else {
            Page<Store> stores = repository.findAll(pageable);
            List<StoreDto> storeDtoList = stores
                    .stream()
                    .map(store -> modelMapper.map(store, StoreDto.class))
                    .toList();
            return new PageImpl<>(storeDtoList, pageable, stores.getTotalElements());
        }
    }
}
