package com.spring.springproject.service.impl;

import com.spring.springproject.dto.StoreDto;
import com.spring.springproject.entities.Store;
import com.spring.springproject.repositories.StoreRepository;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.service.interfaces.StoreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class StoreServiceImpl implements StoreService {

    private final ModelMapper modelMapper;
    private final StoreRepository repository;
    private final TechniqueRepository techniqueRepository;

    @Autowired
    public StoreServiceImpl(ModelMapper modelMapper, StoreRepository repository, TechniqueRepository techniqueRepository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.techniqueRepository = techniqueRepository;
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
        repository.update(modelMapper.map(object, Store.class));
    }

    @Override
    public void delete(Integer id) {
        Store store = repository.findById(id).orElse(null);
        if (store != null) {
            store.getTechniques().forEach(technique -> {
                technique.getStoreList().remove(store);
                techniqueRepository.save(technique);
            });
            repository.deleteById(id);
        }
    }


    @Override
    public void update(Integer id, String name, String address) {
        Store store = repository.findById(id).orElse(null);
        if (store != null) {
            store.setName(name);
            store.setAddress(address);
            repository.update(store);
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
            Page<Store> stores = repository.findAll(Store.builder().name(name).address(address).build(), pageable);
            List<StoreDto> storeDtoList = stores
                    .stream()
                    .map(store -> modelMapper.map(store, StoreDto.class))
                    .toList();
            return new PageImpl<>(storeDtoList, pageable, stores.getTotalElements());
    }
}
